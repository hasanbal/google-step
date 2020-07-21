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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  
    private ArrayList<String> comments = new ArrayList<String>();
  
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String json = new Gson().toJson(comments);
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = getComment(request);
        boolean readable = false;
        if(comment.isEmpty()){
            response.setContentType("text/html");
            response.getWriter().println("Please enter a non-empty comment.");
            return;
        }
        
        for(int i=0;i < comment.length(); i++){
            if(comment.charAt(i) != ' '){
                readable = true;
            }
        }
        if(readable == false){
            response.setContentType("text/html");
            response.getWriter().println("Please enter readable comment.");
            return;
        }


        comments.add(comment);
        response.sendRedirect("/index.html");
    }

    private String getComment(HttpServletRequest request){
        String comment = request.getParameter("comment");
        return comment;
    }

}
