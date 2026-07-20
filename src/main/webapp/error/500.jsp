<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body>
<%@ include file="/includes/navbar.jsp" %>
<div class="container d-flex align-items-center justify-content-center"
     style="min-height:80vh">
  <div class="text-center">
    <div style="font-size:5rem;font-weight:900;color:var(--re-danger);line-height:1">500</div>
    <h2 class="mt-2 mb-3">Server Error</h2>
    <p class="text-muted mb-4" style="max-width:440px;margin:0 auto">
      An unexpected error occurred on our server. Our team has been notified.
      Please try again in a moment.
    </p>
    <a href="<%= request.getContextPath() %>/index.jsp"
       class="btn btn-primary-brand btn-lg-custom">
      <i class="bi bi-house me-2"></i> Return Home
    </a>
  </div>
</div>
<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>