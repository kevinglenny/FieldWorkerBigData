<%-- Generates schema.org microdata that can be parsed to populate snippet
 for photos interactive posts--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="javax.servlet.http.HttpServletRequest"%>
<%@ page import="com.google.devrel.training.conference.domain.Photo"%>
<%@ page import="static com.google.devrel.training.conference.service.OfyService.ofy"%>
<%@ page import="java.util.List"%>
<%

String imageUrl = "";
String name = "";
String description = "";

String photoId = request.getParameter("photoId");
String redirectUrl = "/index.html?photoId=" + photoId;

Photo photo = ofy().load().type(Photo.class).id(Long.parseLong(photoId)).now();
if (photo != null) {
  name = "Photo by " + photo.getOwnerDisplayName() + " for " +
      photo.getThemeDisplayName() + " | Photo Hunt";
  description = photo.getOwnerDisplayName() +
      " needs your vote to win this hunt.";
  imageUrl = photo.getThumbnailUrl();
}
%>
<!DOCTYPE html>
<html>
<head>
  <% if (redirectUrl != null) { %>
  <script type="text/javascript">
    window.location.href = '<%= redirectUrl %>';
  </script>
  <% } %>
  <title><%= name %></title>
</head>
<body itemscope itemtype="http://schema.org/Thing">
  <h1 itemprop="name"><%= name %></h1>
  <img itemprop="image" src="<%= imageUrl %>" />
  <p itemprop="description"><%= description %></p>
</body>
</html>