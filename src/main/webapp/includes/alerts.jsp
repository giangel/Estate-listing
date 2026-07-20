<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Retrieve and clear flash messages from session
    String successMsg = (String) session.getAttribute("successMessage");
    String errorMsg   = (String) session.getAttribute("errorMessage");
    if (successMsg != null) session.removeAttribute("successMessage");
    if (errorMsg   != null) session.removeAttribute("errorMessage");
%>
<% if (successMsg != null && !successMsg.isEmpty()) { %>
<div class="re-alert re-alert-success" id="flashSuccess" role="alert">
  <i class="bi bi-check-circle-fill"></i>
  <div><%= successMsg %></div>
  <button type="button" class="btn-close ms-auto" style="font-size:0.75rem"
          onclick="this.parentElement.style.display='none'"></button>
</div>
<% } %>

<% if (errorMsg != null && !errorMsg.isEmpty()) { %>
<div class="re-alert re-alert-error" id="flashError" role="alert">
  <i class="bi bi-exclamation-circle-fill"></i>
  <div><%= errorMsg %></div>
  <button type="button" class="btn-close ms-auto" style="font-size:0.75rem"
          onclick="this.parentElement.style.display='none'"></button>
</div>
<% } %>

<script>
  // Auto-dismiss alerts after 5 seconds
  setTimeout(function() {
    var s = document.getElementById('flashSuccess');
    var e = document.getElementById('flashError');
    if (s) s.style.display = 'none';
    if (e) e.style.display = 'none';
  }, 5000);
</script>