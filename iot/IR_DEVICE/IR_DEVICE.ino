#include <ESP8266WiFi.h>

#include <PubSubClient.h>

#include <WiFiClient.h>
#include <EEPROM.h>
#include <ArduinoJson.h>
#include <IRremoteESP8266.h>
#include <IRsend.h>
#include <IRutils.h>

#define _DEBUG_        1

#if _DEBUG_ == 1
    #define BEGIN_DEBUG(a) Serial.begin(a)
    #define DBG_PRINT(a) Serial.print(a)
    #define DBG_PRINTLN(a) Serial.println(a)
#else
    #define BEGIN_DEBUG(a)
    #define DBG_PRINT(a)
    #define DBG_PRINTLN(a)
#endif

#define CAPTURE_BUFFER_SIZE     1024
#define TIMEOUT                 15U
#define MIN_UNKNOWN_SIZE        12

#define PIN_IR_RECV             14
#define PIN_IR_SEND             12

#define LEN_FIELD               20   // 필드 크기

/*
 * DATA STRUCTURE
 */
struct ESP_SETUP
{
    char wifiSsid[LEN_FIELD];
    char wifiPwd[LEN_FIELD];
    char mqttId[LEN_FIELD*2];
    char mqttPwd[LEN_FIELD*2];
    char mqttIp[LEN_FIELD*3];
    int mqttPort;
    char clientId[LEN_FIELD*3];
};

/*
 * IR 수신
 */
IRrecv irrecv(PIN_IR_RECV, CAPTURE_BUFFER_SIZE, TIMEOUT, true);
decode_results results;  // Somewhere to store the results

/*
 * IR 송신
 */
IRsend irsend(PIN_IR_SEND);

/*
 * GLOBAL VARIABLE
 */
struct ESP_SETUP config;

WiFiClient wifiClient;

PubSubClient * mqttClient;

const char * TOPIC_SUBSCRIBE    = "home/ir/cmd/control";    // subscribe
const char * TOPIC_PUBLISH      = "home/ir/evt/status";     // publish

boolean flagWifi                = false;
boolean flagMqtt                = false;

unsigned long wifiTry           = 0;
unsigned long mqttTry           = 0;

unsigned long lastSendTime      = 0;
unsigned long captureTime       = 0;

boolean connectEvent            = false;
int blinkCount                  = 0;
unsigned long blinkTime         = 0;
boolean blinkFlag               = true;

void checkNetwork();
void checkMQTT();
void mqttCallback(char* topic, byte* payload, unsigned int length);

/**
 *
 */
void sendIR(uint16_t *rawData, int len)
{
    irsend.sendRaw(rawData, len, 38);  // Send a raw data capture at 38kHz.

    yield();  // Feed the WDT (again)

    digitalWrite(LED_BUILTIN, LOW);
    delay(250);
    digitalWrite(LED_BUILTIN, HIGH);
}

/**
 * 리모콘 신호를 숫자 문자열로 만든다.
 */
String makePayload(const decode_results *results)
{
    String output = "";
    // Type
    output += typeToString(results->decode_type, results->repeat);
    output +="|";

    // Value
    output += uint64ToString(results->value, 16);
    output += "|";

    output += uint64ToString(getCorrectedRawLength(results), 10);
    output += "|";

    // Dump data
    for (uint16_t i = 1; i < results->rawlen; i++) {
        uint32_t usecs;
        for (usecs = results->rawbuf[i] * RAWTICK; usecs > UINT16_MAX; usecs -= UINT16_MAX) {
            output += uint64ToString(UINT16_MAX);
            if (i % 2)
                output += ",0,";
            else
                output += ",0,";
        }
        output += uint64ToString(usecs, 10);
        if (i < results->rawlen - 1)
            output += ",";
    }

    sendMessage("IR", output.c_str());

    return output;
}

/*
 * 초기화
 */
void setup()
{
    BEGIN_DEBUG(115200);

    pinMode(LED_BUILTIN, OUTPUT);
    
    memset(&config, 0x00, sizeof(struct ESP_SETUP));

    strcpy(config.wifiSsid, "VirusAP");
    strcpy(config.wifiPwd,  "0205062500");
    strcpy(config.mqttIp,   "192.168.1.4");
    config.mqttPort = 1883;
    strcpy(config.mqttId,   "hiot");
    strcpy(config.mqttPwd,  "hiot");
    strcpy(config.clientId, "IR_DEVICE");

    WiFi.mode(WIFI_STA);

    irrecv.enableIRIn();  // Start the receiver
    irsend.begin();
}

/*
 * 반복 수행
 */
void loop()
{
    if ( connectEvent == true )
    {
        if ( millis() - blinkTime >= 300 )
        {
            if ( blinkFlag == true ) {
                blinkFlag = false;
                digitalWrite(LED_BUILTIN, LOW);
            }
            else {
                blinkFlag = true;
                digitalWrite(LED_BUILTIN, HIGH);
                blinkCount++;
            }

            blinkTime = millis();
        }

        if ( blinkCount >= 5 )
        {
            connectEvent = false;
        }
    }

    checkNetwork();

    if ( flagMqtt == true && connectEvent == false )
    {
        if ( millis() - lastSendTime >= 1800000UL )
        {
            sendMessage("STATUS", "ALIVE");
            lastSendTime = millis();
        }
    }

    if (irrecv.decode(&results))
    {
        if ( millis() - captureTime > 1000 ) {
            makePayload(&results);
            captureTime = millis();
        }
        
        irrecv.resume();  // Receive the next value
    }
}

char gBuffer[2048];
StaticJsonBuffer<8096> gJsonBuffer;

/*
 * subscribe
 */
void mqttCallback(char* topic, byte* payload, unsigned int length)
{
    int len = length >= 2048 ? 2047 : length;
    memcpy(gBuffer, payload, len);
    gBuffer[len] = 0;

    DBG_PRINT(">> Topic: ");
    DBG_PRINTLN(topic);
    DBG_PRINT(">> Len: ");
    DBG_PRINTLN(len);
    DBG_PRINT(">> Payload: ");
    DBG_PRINTLN(gBuffer);

    JsonObject& root = gJsonBuffer.parseObject(gBuffer);
    JsonArray& cmd = root["cmd"];

    for(int i = 0; i < cmd.size(); i++)
    {
        JsonObject& elm = cmd.get(i);
        int idx = 0;
        int rawLen = elm["rawLen"];
        const char * data = elm["rawData"];
        uint16_t * rawData = (uint16_t *)malloc(sizeof(uint16_t) * rawLen);

        char * pch = strtok ((char *)data, ",");
        while (pch != NULL)
        {
            rawData[idx++] = strtoul(pch, NULL, 10);
            DBG_PRINTLN(rawData[idx-1]);
            pch = strtok (NULL, ",");
        }

        sendIR(rawData, rawLen);
        free(rawData);
    }
}

/*
 * MQTT 연결을 체크한다.
 */
void checkMQTT()
{
    if ( mqttClient != NULL && mqttClient->connected() == false )
    {
        DBG_PRINTLN("MQTT Disconnected");
        flagMqtt = false;
        mqttTry = 0;
    }
}

/*
 * Wifi 연결 및 MQTT 연결을 유지한다.
 */
void checkNetwork()
{
     if ( flagWifi == false )
     {
       if ( wifiTry == 0 || millis() - wifiTry >= 20000UL ) // 20sec
       {
           DBG_PRINT("\r\n>> Connecting to ");
           DBG_PRINTLN(config.wifiSsid);
           WiFi.begin(config.wifiSsid, config.wifiPwd);
           wifiTry = millis();
       }

       if ( WiFi.status() == WL_CONNECTED )
       {
         flagWifi = true;
         DBG_PRINT(">>Connected, IP =  ");
         DBG_PRINTLN(WiFi.localIP());
       }
  }
  else {
    delay(1);
    switch(WiFi.status())
    {
        //case WL_CONNECT_FAILED:
        //case WL_CONNECTION_LOST:
        case WL_DISCONNECTED:
        DBG_PRINT(">>Disconnected : wifi ");
        flagWifi = false;
        flagMqtt = false;
        wifiTry = 0;
        mqttTry = 0;
        break;
    }
  }

  if ( flagWifi == true )
  {
    if ( mqttClient == NULL ) {
        mqttClient = new PubSubClient(config.mqttIp, config.mqttPort, mqttCallback, wifiClient);
    }

    if ( flagMqtt == false )
    {
        if ( mqttTry == 0 || millis() - mqttTry >= 10000UL ) // 10sec
        {
            if ( mqttClient->connect(config.clientId, config.mqttId, config.mqttPwd) )
            {
                flagMqtt = true;
                DBG_PRINTLN("Connected to MQTT broker");

                if (mqttClient->subscribe(TOPIC_SUBSCRIBE, 1)) {
                    DBG_PRINTLN("Subscribe ok");
                    connectEvent = true;
                    blinkCount = 0;
                    blinkTime = 0;
                    blinkFlag = true;

                    sendMessage("STATUS", "CONNECT");
                }
                else {
                    DBG_PRINTLN("Subscribe failed");
                }
            }
            else {
                DBG_PRINTLN("MQTT connect failed");
            }

            mqttTry = millis();
        }
    }
    else {
        if ( mqttClient->connected() == true )
        {
            mqttClient->loop();
        }
        else {
            flagMqtt = false;
            mqttTry = 0;
        }
    }
  }
}

/**
 * MQTT 메시지를 전송한다.
 */
char gBuffer2[2048];
StaticJsonBuffer<8096> gJsonBuffer2;

void sendMessage(char *type, const char *raw)
{
    if ( flagMqtt == false )
        return;

    JsonObject& payload = gJsonBuffer2.createObject();

    payload["type"] = type;
    payload["raw"] = raw;

    memset(gBuffer2, 0x00, sizeof(gBuffer2));
    payload.printTo(gBuffer2, 8095);

    if (mqttClient->publish(TOPIC_PUBLISH, gBuffer2)) {
        DBG_PRINTLN("Publish ok");
    }
    else {
       DBG_PRINTLN("Publish failed");
       checkMQTT();
    }
}
