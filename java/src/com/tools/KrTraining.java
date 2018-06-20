package com.tools;

import java.io.ByteArrayInputStream;

import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.Example;
import com.ibm.watson.developer_cloud.conversation.v1.model.ExampleCollection;
import com.ibm.watson.developer_cloud.conversation.v1.model.IntentCollection;
import com.ibm.watson.developer_cloud.conversation.v1.model.IntentExport;
import com.ibm.watson.developer_cloud.conversation.v1.model.ListExamplesOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.ListIntentsOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Corpus;
import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * STT 서비스를 WCS Intent 문장을 이용하여 학습한다.
 */
public class KrTraining
{
	public static final String MODEL_NAME = "kr_homeai";
	public static final String CORPUS_NAME = "homeai";
	
	public KrTraining() {
	}
	
	/**
	 * @return
	 */
	private String getUtterance() throws Exception
	{
		StringBuffer examples = new StringBuffer();
		
		String username = AppsPropertiy.getInstance().getProperty("wcs.user");
		String password = AppsPropertiy.getInstance().getProperty("wcs.passwd");
		String workspaceId = AppsPropertiy.getInstance().getProperty("wcs.workid");

		Conversation service = new Conversation("2018-02-16");
		service.setUsernameAndPassword(username, password);

		ListIntentsOptions options = new ListIntentsOptions.Builder(workspaceId).build();
		IntentCollection response = service.listIntents(options).execute();

		for(IntentExport ee : response.getIntents())
		{
			ListExamplesOptions opts = new ListExamplesOptions.Builder(workspaceId, ee.getIntentName()).build();

			ExampleCollection exList = service.listExamples(opts).execute();

			for(Example e : exList.getExamples())
				examples.append(e.getExampleText()).append("\n");
		}
		
		return examples.toString();
	}
	
	/**
	 * 
	 */
	public void training(String custId)
	{
		try {
			String username = AppsPropertiy.getInstance().getProperty("stt.watson.user");
			String password = AppsPropertiy.getInstance().getProperty("stt.watson.passwd");
			
			SttUtility sttUtil = new SttUtility(username, password);

			if ( custId == null ) {
				String examples = getUtterance();
				
				custId = sttUtil.createModel(MODEL_NAME, MODEL_NAME);
				
				sttUtil.addCorpus(custId, CORPUS_NAME, new ByteArrayInputStream(examples.getBytes()));
				
				System.out.println("* Customization Id = " + custId);
				
				long time = System.currentTimeMillis();
				do {
					Corpus corpus = sttUtil.getCorpus(custId, CORPUS_NAME);
					if ( "analyzed".equals(corpus.getStatus()) ) {
						break;
					}

					Thread.sleep(10000);
				}while(System.currentTimeMillis()-time <= 60000 );
				
				sttUtil.training(custId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String [] args)
	{
		(new KrTraining()).training(null);
	}
}
