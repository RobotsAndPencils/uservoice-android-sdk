package com.uservoice.uservoicesdk.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.uservoice.uservoicesdk.rest.Callback;
import com.uservoice.uservoicesdk.rest.RestTaskCallback;

public class Suggestion extends BaseModel {
	
	private String title;
	private String text;
	private String status;
	private String statusColor;
	private String creatorName;
	private String adminResponseText;
	private String adminResponseUserName;
	private String adminResponseAvatarUrl;
	private Date adminResponseCreatedAt;
	private Date createdAt;
	private Category category;
	private int numberOfComments;
	private int numberOfVotes;
	private int numberOfVotesByCurrentUser;
	private int numberOfVotesRemainingForCurrentUser;
	private int forumId;

	public static void loadSuggestions(Forum forum, int page, final Callback<List<Suggestion>> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", String.valueOf(page));
		params.put("per_page", "20");
		params.put("filter", "public");
		params.put("sort", getClientConfig().getSuggestionSort());
		doGet(apiPath("/forums/%d/suggestions.json", forum.getId()), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject object) throws JSONException {
				callback.onModel(deserializeList(object, "suggestions", Suggestion.class));
			}
		});
	}
	
	public static void searchSuggestions(Forum forum, String query, final Callback<List<Suggestion>> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("query", query);
		doGet(apiPath("/forum/%d/suggestions.json", forum.getId()), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject object) throws JSONException {
				callback.onModel(deserializeList(object, "suggestions", Suggestion.class));
			}
		});
	}
	
	public static void createSuggestion(Forum forum, Category category, String title, String text, int numberOfVotes, final Callback<Suggestion> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("suggestion[votes]", String.valueOf(numberOfVotes));
		params.put("suggestion[title]", title);
		params.put("suggestion[text]", text);
		params.put("suggestion[category_id]", String.valueOf(category.getId()));
		doPost(apiPath("/forum/%d/suggestions.json", forum.getId()), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject object) throws JSONException {
				callback.onModel(deserializeObject(object, "suggestion", Suggestion.class));
			}
		});
	}
	
	public void vote(int numberOfVotes, final Callback<Suggestion> callback) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("to", String.valueOf(numberOfVotes));
		doPost(apiPath("/forums/%d/suggestions/%d/votes.json", forumId, id), params, new RestTaskCallback(callback) {
			@Override
			public void onComplete(JSONObject result) throws JSONException {
				callback.onModel(deserializeObject(result, "suggestion", Suggestion.class));
			}
		});
	}
	
	@Override
	public void load(JSONObject object) throws JSONException {
		super.load(object);
		title = getString(object, "title");
		text = getString(object, "text");
		createdAt = getDate(object, "created_at");
		forumId = object.getJSONObject("topic").getJSONObject("forum").getInt("id");
		if (!object.isNull("category"))
			category = deserializeObject(object, "category", Category.class);
		numberOfComments = object.getInt("comments_count");
		numberOfVotes = object.getInt("vote_count");
		creatorName = getString(object.getJSONObject("creator"), "name");
		if (!object.isNull("votes_for"))
			numberOfVotesByCurrentUser = object.getInt("votes_for");
		if (!object.isNull("votes_remaining"))
			numberOfVotesRemainingForCurrentUser = object.getInt("votes_remaining");
		if (!object.isNull("status")) {
			JSONObject statusObject = object.getJSONObject("status");
			status = getString(statusObject, "name");
			statusColor = getString(statusObject, "hex_color");
		}
		if (!object.isNull("response")) {
			JSONObject response = object.getJSONObject("response");
			adminResponseText = getString(response, "text");
			adminResponseCreatedAt = getDate(response, "created_at");
			JSONObject responseUser = response.getJSONObject("creator");
			adminResponseUserName = getString(responseUser, "name");
			adminResponseAvatarUrl = getString(responseUser, "avatar_url");
		}
	}
	
	public int getForumId() {
		return forumId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getStatusColor() {
		return statusColor;
	}
	
	public String getCreatorName() {
		return creatorName;
	}

	public String getAdminResponseText() {
		return adminResponseText;
	}

	public String getAdminResponseUserName() {
		return adminResponseUserName;
	}

	public String getAdminResponseAvatarUrl() {
		return adminResponseAvatarUrl;
	}

	public Date getAdminResponseCreatedAt() {
		return adminResponseCreatedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Category getCategory() {
		return category;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public int getNumberOfVotes() {
		return numberOfVotes;
	}

	public int getNumberOfVotesByCurrentUser() {
		return numberOfVotesByCurrentUser;
	}

	public int getNumberOfVotesRemainingForCurrentUser() {
		return numberOfVotesRemainingForCurrentUser;
	}
	
}