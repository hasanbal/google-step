// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    ArrayList<String> comments = new ArrayList<String>();

    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comment");
      String username = (String) entity.getProperty("username");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment curComment = new Comment(username, comment, timestamp);
      String commentJson = new Gson().toJson(curComment);

      comments.add(commentJson);
    }

    response.setContentType("application/json");
    String json = new Gson().toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String comment = getComment(request);
    String username = "anonymous";

    boolean readableComment = false;
    boolean readableUsername = false;

    if (userService.isUserLoggedIn()) {
      username = userService.getCurrentUser().getEmail();
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity commentEntity = new Entity("Comment");
    long timestamp = System.currentTimeMillis();

    if (comment.isEmpty() || username.isEmpty()) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter a non-empty comment and username.");
      return;
    }

    for (int i = 0; i < comment.length(); i++) {
      if (comment.charAt(i) != ' ') {
        readableComment = true;
      }
    }

    for (int i = 0; i < username.length(); i++) {
      if (username.charAt(i) != ' ') {
        readableUsername = true;
      }
    }

    if (readableComment == false || readableUsername == false) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter readable comment and username.");
      return;
    }

    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("username", username);
    commentEntity.setProperty("timestamp", timestamp);

    datastore.put(commentEntity);
    response.sendRedirect("/index.html");
  }

  private String getComment(HttpServletRequest request) {
    String comment = request.getParameter("comment");
    return comment;
  }

  private String getUsername(HttpServletRequest request) {
    String username = request.getParameter("username");
    return username;
  }
}
