<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% request.setAttribute("pageTitle","Forgot Password"); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
</head>
<body data-ctx="<%= request.getContextPath() %>">
<jsp:include page="/includes/head-meta.jsp" />

<div class="container py-5" style="min-height:80vh;display:flex;align-items:center">
  <div class="row w-100 justify-content-center">
    <div class="col-lg-5 col-md-7">

      <div class="text-center mb-4">
        <div style="width:64px;height:64px;border-radius:16px;
                    background:rgba(232,160,32,0.1);
                    border:2px solid rgba(232,160,32,0.3);
                    display:flex;align-items:center;justify-content:center;
                    margin:0 auto 1rem">
          <i class="bi bi-key-fill" style="font-size:1.8rem;color:var(--re-secondary)"></i>
        </div>
        <c:choose>
          <c:when test="${not empty token}">
            <h3>Set New Password</h3>
            <p class="text-muted">Enter and confirm your new password below.</p>
          </c:when>
          <c:otherwise>
            <h3>Forgot Your Password?</h3>
            <p class="text-muted">Enter your email address and we'll send you a reset link.</p>
          </c:otherwise>
        </c:choose>
      </div>

      <div class="card border-0 shadow-lg rounded-4 p-4">
        <div class="card-body">

          <c:if test="${not empty requestScope.successMessage}">
            <div class="re-alert re-alert-success">
              <i class="bi bi-check-circle-fill"></i>
              <c:out value="${requestScope.successMessage}"/>
            </div>
          </c:if>
          <c:if test="${not empty requestScope.errorMessage}">
            <div class="re-alert re-alert-error">
              <i class="bi bi-exclamation-circle-fill"></i>
              <c:out value="${requestScope.errorMessage}"/>
            </div>
          </c:if>

          <c:choose>

            <%-- STEP 1: Enter email --%>
            <c:when test="${empty token}">
              <form action="<%= request.getContextPath() %>/forgot-password" method="POST">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                <input type="hidden" name="action" value="request">
                
                <div class="re-form-group">
                  <label>Email Address <span class="required">*</span></label>
                  <input type="email" class="re-form-control" name="email"
                         placeholder="your@email.com" required autofocus>
                </div>
                
                <button type="submit" class="btn btn-secondary-brand btn-lg-custom w-100">
                  <i class="bi bi-send me-1"></i> Send Reset Link
                </button>
              </form>
            </c:when>

            <%-- STEP 2: Set new password --%>
            <c:otherwise>
              <form action="<%= request.getContextPath() %>/forgot-password" method="POST">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                <input type="hidden" name="action" value="reset">
                <input type="hidden" name="token" value="<c:out value='${token}' escapeXml='true'/>">
                
                <div class="re-form-group">
                  <label>New Password <span class="required">*</span></label>
                  <input type="password" class="re-form-control"
                         id="newPassword" name="newPassword"
                         placeholder="Min 8 chars with A-Z, 0-9, @#!" required>
                </div>
                
                <div class="re-form-group">
                  <label>Confirm Password <span class="required">*</span></label>
                  <input type="password" class="re-form-control"
                         id="confirmPassword" name="confirmPassword"
                         placeholder="Repeat password" required>
                </div>
                
                <button type="submit" class="btn btn-primary-brand btn-lg-custom w-100">
                  <i class="bi bi-lock me-1"></i> Reset Password
                </button>
              </form>
            </c:otherwise>

          </c:choose>

          <div class="text-center mt-3">
            <a href="<%= request.getContextPath() %>/login"
               style="font-size:0.875rem;color:var(--re-gray-500)">
              <i class="bi bi-arrow-left me-1"></i> Back to Login
            </a>
          </div>

        </div>
      </div>

    </div>
  </div>
</div>

<jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/dark-mode.js"></script>
</body>
</html>