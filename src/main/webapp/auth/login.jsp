<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    request.setAttribute("pageTitle", "Login");
    String ctx = request.getContextPath();
    // Redirect if already logged in
    if (session.getAttribute("loggedInUser") != null) {
        response.sendRedirect(ctx + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
</head>
<body data-ctx="<%= ctx %>">

<jsp:include page="/includes/navbar.jsp" />

<div class="container py-5">
  <div class="row w-100 justify-content-center align-items-center g-4">

    <!-- Left Panel - Branding and Core Value Propositions -->
    <div class="col-lg-5 d-none d-lg-flex align-items-center pe-lg-5">
      <div>
        <div class="mb-4" style="width:64px;height:64px;background:var(--re-primary);
             border-radius:16px;display:flex;align-items:center;justify-content:center">
          <i class="bi bi-house-door-fill" style="font-size:2rem;color:white"></i>
        </div>
        <h2 class="mb-3">Welcome Back to<br>AOP Real Estate</h2>
        <p class="text-muted mb-4">
          Log in to access your dashboard, manage listings, track inquiries,
          and find your perfect home near campus.
        </p>
        <div class="d-flex flex-column gap-3">
          <div class="d-flex align-items-center gap-3">
            <div style="width:40px;height:40px;background:rgba(25,135,84,0.1);
                 border-radius:10px;display:flex;align-items:center;justify-content:center">
              <i class="bi bi-patch-check-fill" style="color:#198754"></i>
            </div>
            <span style="font-size:0.9rem">Verified property listings</span>
          </div>
          <div class="d-flex align-items-center gap-3">
            <div style="width:40px;height:40px;background:rgba(13,202,240,0.1);
                 border-radius:10px;display:flex;align-items:center;justify-content:center">
              <i class="bi bi-people-fill" style="color:#0dcaf0"></i>
            </div>
            <span style="font-size:0.9rem">Roommate matching for students</span>
          </div>
          <div class="d-flex align-items-center gap-3">
            <div style="width:40px;height:40px;background:rgba(232,160,32,0.1);
                 border-radius:10px;display:flex;align-items:center;justify-content:center">
              <i class="bi bi-shield-fill-check" style="color:#e8a020"></i>
            </div>
            <span style="font-size:0.9rem">Fraud-protected platform</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Right Panel - Form Card Identity Verification Component -->
    <div class="col-lg-5 col-md-8 col-12">
      <div class="card border-0 shadow-lg rounded-4 p-4">
        <div class="card-body">

          <h3 class="mb-1">Sign In</h3>
          <p class="text-muted mb-4" style="font-size:0.9rem">
            Enter your credentials to access your account.
          </p>

          <%@ include file="/includes/alerts.jsp" %>

          <c:if test="${not empty requestScope.errorMessage}">
            <div class="re-alert re-alert-error mb-3">
              <i class="bi bi-exclamation-circle-fill"></i>
              <c:out value="${requestScope.errorMessage}"/>
            </div>
          </c:if>

          <form action="<%= ctx %>/login" method="POST" novalidate>
            
            <!-- Standardized and Protected CSRF Verification State Token -->
            <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">

            <!-- Security Filter Destination Target Tracking Redirection Parameter -->
            <c:if test="${not empty param.redirect}">
              <input type="hidden" name="redirect" value="<c:out value='${param.redirect}'/>">
            </c:if>

            <!-- Email Address Interactive Control Input -->
            <div class="re-form-group mb-3">
              <label for="email" class="form-label">
                Email Address <span class="required" style="color:var(--re-danger)">*</span>
              </label>
              <div style="position:relative">
                <i class="bi bi-envelope"
                   style="position:absolute;left:14px;top:50%;
                          transform:translateY(-50%);color:var(--re-gray-500)"></i>
                <input type="email" class="re-form-control" id="email" name="email"
                       style="padding-left:2.5rem"
                       value="<c:out value='${emailValue}'/>"
                       placeholder="your@email.com" required autofocus>
              </div>
            </div>

            <!-- Password Vault Interactive Control Input -->
            <div class="re-form-group mb-3">
              <label for="password" class="form-label">
                Password <span class="required" style="color:var(--re-danger)">*</span>
              </label>
              <div style="position:relative">
                <i class="bi bi-lock"
                   style="position:absolute;left:14px;top:50%;
                          transform:translateY(-50%);color:var(--re-gray-500)"></i>
                <input type="password" class="re-form-control" id="password"
                       name="password" style="padding-left:2.5rem"
                       placeholder="Enter your password" required>
                <button type="button"
                        style="position:absolute;right:14px;top:50%;
                               transform:translateY(-50%);background:none;
                               border:none;color:var(--re-gray-500);cursor:pointer"
                        onclick="togglePasswordVisibility('password', this)"
                        aria-label="Toggle password visibility view status">
                  <i class="bi bi-eye"></i>
                </button>
              </div>
            </div>

            <!-- Persistent State Control Configuration options -->
            <div class="d-flex align-items-center justify-content-between mb-4">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="rememberMe" name="rememberMe">
                <label class="form-check-label" for="rememberMe" style="font-size:0.85rem">
                  Remember me
                </label>
              </div>
              <a href="<%= ctx %>/forgot-password" style="font-size:0.85rem;color:var(--re-secondary)">
                Forgot password?
              </a>
            </div>

            <!-- Execution Submissions Router Trigger Control -->
            <button type="submit" class="btn btn-primary-brand btn-lg-custom w-100 mb-3">
              <i class="bi bi-box-arrow-in-right me-2"></i> Sign In
            </button>

            <p class="text-center mb-0" style="font-size:0.875rem">
              Don't have an account?
              <a href="<%= ctx %>/register" style="color:var(--re-secondary);font-weight:600">
                Register here
              </a>
            </p>

          </form>
        </div>
      </div>
    </div>

  </div>
</div>

<jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script>
function togglePasswordVisibility(fieldId, btn) {
  var field = document.getElementById(fieldId);
  var icon  = btn.querySelector('i');
  if (field.type === 'password') {
    field.type = 'text';
    icon.className = 'bi bi-eye-slash';
  } else {
    field.type = 'password';
    icon.className = 'bi bi-eye';
  }
}
</script>
</body>
</html>