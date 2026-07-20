<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <% request.setAttribute("pageTitle","Access Denied"); %>
</head>
<body>
<%@ include file="/includes/navbar.jsp" %>
<div class="container d-flex align-items-center justify-content-center"
     style="min-height:80vh">
  <div class="text-center">
    <div style="font-size:5rem;font-weight:900;color:var(--re-primary);line-height:1">403</div>
    <h2 class="mt-2 mb-3">Access Denied</h2>
    <p class="text-muted mb-4" style="max-width:400px;margin:0 auto">
      You do not have permission to access this page.
      Please log in with the appropriate account.
    </p>
    <a href="<%= request.getContextPath() %>/index.jsp"
       class="btn btn-primary-brand btn-lg-custom me-2">
      <i class="bi bi-house me-2"></i> Go Home
    </a>
<a href="<%= request.getContextPath() %>/login"
       class="btn btn-outline-brand btn-lg-custom">
      <i class="bi bi-box-arrow-in-right me-2"></i> Login
    </a>
  </div>
</div>
<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>