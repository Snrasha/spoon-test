package spoon.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class GitpullRequest {

	private String nameUser;
	private String nameProject;
	private String branch;

	public GitpullRequest(String nameUser, String nameProject, String branch) {
		this.nameProject = nameProject; // spoon-test
		this.nameUser = nameUser; // Snrasha
		this.branch = branch; // de
	}

	public void getData() throws IllegalStateException, IOException {
		String url = "http://api.github.com/repos/"+this.nameUser+"/"+this.nameProject+"/pulls";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// add header
		post.addHeader("Authorization", " token ");

		StringEntity params = new StringEntity("{ \"title\": \"Amazing new feature\","
				+ "\"body\": \"Please pull this in!\"," + "\"head\": \""+this.nameUser+":"+this.branch+"\"," + "\"base\": \""+"master"+"\" }",
				ContentType.APPLICATION_JSON);
		post.setEntity(params);

		HttpResponse response = client.execute(post);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
	}
}