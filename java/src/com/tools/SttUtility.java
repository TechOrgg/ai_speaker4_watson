package com.tools;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.AddCorpusOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.AddWordsOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Corpora;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Corpus;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.CreateLanguageModelOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.DeleteCorpusOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.DeleteLanguageModelOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.GetCorpusOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.LanguageModel;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.LanguageModels;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.ListCorporaOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.ListWordsOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.TrainLanguageModelOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Word;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.Words;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * STT 학습과 관련된 기능을 모아둔 클래스
 * Java API Doc : http://watson-developer-cloud.github.io/java-sdk/docs/java-sdk-5.1.1/
 *
 */
public class SttUtility
{
	static class ModelEntry
	{
		public String modelName;
		public String customizationId;
		
		public String toString()
		{
			return modelName + "::" + customizationId;
		}
	}
	
	
	static class CorpusEntry
	{
		String corpusName;
		
	}
	
	private SpeechToText service;
	
	public SttUtility(String username, String password)
	{
		service = new SpeechToText();
		service.setUsernameAndPassword(username, password);
	}
	

	/**
	 * @return
	 */
	public String createModel(String name, String desc)
	{
		CreateLanguageModelOptions ops = new CreateLanguageModelOptions.Builder()
				.name(name)
				.baseModelName("ko-KR_BroadbandModel")
				.description(desc)
				.build();
		
		LanguageModel myModel = service.createLanguageModel(ops).execute();
		
		System.out.println(myModel);
		
		return myModel.getCustomizationId();
	}
	
	/**
	 * @return
	 */
	public List<ModelEntry> listModel()
	{
		List<ModelEntry> list = new ArrayList<ModelEntry>();
		
		LanguageModels model = service.listLanguageModels().execute();
		
		for(LanguageModel m : model.getCustomizations()) {
			ModelEntry me = new ModelEntry();
			me.modelName = m.getName();
			me.customizationId = m.getCustomizationId();
			
			list.add(me);
		}
		
		return list;
	}
	
	/**
	 * @param customizationId
	 */
	public void deleteModel(String customizationId)
	{
		DeleteLanguageModelOptions opts = new DeleteLanguageModelOptions.Builder()
				.customizationId(customizationId)
				.build();
		
		service.deleteLanguageModel(opts).execute();
	}
	
	/**
	 * @param customizationId
	 * @param corpName
	 * @param file
	 * @throws Exception
	 */
	public void addCorpus(String customizationId, String corpusName, String file) throws Exception
	{
		AddCorpusOptions opts = new AddCorpusOptions.Builder()
				.corpusFile(new File(file))
				.allowOverwrite(true)
				.corpusName(corpusName)
				.customizationId(customizationId)
				.build();
		
		service.addCorpus(opts).execute();
	}
	
	/**
	 * @param customizationId
	 * @param corpName
	 * @param input
	 * @throws Exception
	 */
	public void addCorpus(String customizationId, String corpusName, InputStream input) throws Exception
	{
		AddCorpusOptions opts = new AddCorpusOptions.Builder()
				.corpusFile(input)
				.allowOverwrite(true)
				.corpusName(corpusName)
				.customizationId(customizationId)
				.build();
		
		service.addCorpus(opts).execute();
	}
	
	/**
	 * @param custId
	 * @param corpusName
	 * @return
	 */
	public Corpus getCorpus(String customizationId, String corpusName)
	{
		GetCorpusOptions opts = new GetCorpusOptions.Builder()
				.customizationId(customizationId)
				.corpusName(corpusName)
				.build();
		
		Corpus corpus = service.getCorpus(opts).execute();
		
		return corpus;
	}
	
	/**
	 * @param customizationId
	 * @return
	 */
	public List<String> listCorpora(String customizationId)
	{
		List<String> list = new ArrayList<String>();
		
		ListCorporaOptions opts = new ListCorporaOptions.Builder()
				.customizationId(customizationId)
				.build();
		
		Corpora corpora = service.listCorpora(opts).execute();

		for(Corpus c : corpora.getCorpora()) {
			list.add(c.getName());
		}
		
		return list;
	}
	
	/**
	 * @param custId
	 * @param corpusName
	 */
	public void deleteCorpus(String custId, String corpusName)
	{
		DeleteCorpusOptions opts = new DeleteCorpusOptions.Builder()
				.customizationId(custId)
				.corpusName(corpusName)
				.build();
		
		service.deleteCorpus(opts).execute();
	}
	
	
	/**
	 * @param customizationId
	 * @return
	 */
	public List<String> listWord(String customizationId)
	{
		List<String> list = new ArrayList<String>();
		
		ListWordsOptions opts = new ListWordsOptions.Builder()
				.customizationId(customizationId)
				.build();
		
		Words words = service.listWords(opts).execute();

		for(Word w : words.getWords()) {
			list.add(w.getWord());
		}
		
		return list;
	}
	
	/**
	 * @param customizationId
	 * @param corpName
	 * @param file
	 * @throws Exception
	 */
	public void addWords(String customizationId, String corpusName, String file) throws Exception
	{
		AddWordsOptions opts = new AddWordsOptions.Builder()
				.customizationId(customizationId)
				.build();
		
		service.addWords(opts);
	}
	
	/**
	 * @param customizationId
	 */
	public void training(String customizationId)
	{
		TrainLanguageModelOptions opt = new TrainLanguageModelOptions.Builder()
				.customizationId(customizationId)
				.build();
		
		service.trainLanguageModel(opt).execute();
	}
}
