<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
    request.setAttribute("pageTitle", "Create Account"); 
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <jsp:include page="/includes/head-meta.jsp" />
</head>
<body data-ctx="<%= contextPath %>">
<jsp:include page="/includes/navbar.jsp" />

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-8 col-md-10">

      <div class="text-center mb-4">
        <h2 class="mb-1">Create Your Account</h2>
        <p class="text-muted">Join AOPE's trusted accommodation platform.</p>
      </div>

      <div class="card border-0 shadow-lg rounded-4 p-4">
        <div class="card-body">

          <%@ include file="/includes/alerts.jsp" %>

          <c:if test="${not empty requestScope.errorMessage}">
            <div class="re-alert re-alert-error mb-3">
              <i class="bi bi-exclamation-circle-fill"></i>
              <c:out value="${requestScope.errorMessage}"/>
            </div>
          </c:if>

          <form action="<%= contextPath %>/register" method="POST" novalidate>
     
            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
            <div class="re-form-group">
              <label class="form-label fw-bold mb-2">Account Type <span class="required" style="color:var(--re-danger)">*</span></label>
              <div class="row g-2 mt-1">
                <div class="col-6 col-md-3">
                  <input type="radio" class="btn-check" name="role" id="roleStudent" value="2" ${requestScope.roleValue == '2' || empty requestScope.roleValue ? 'checked' : ''}>
                  <label class="btn w-100 btn-outline-secondary rounded-3 py-3" for="roleStudent" style="font-size:0.82rem">
                    <i class="bi bi-mortarboard-fill d-block mb-1" style="font-size:1.4rem; color:var(--re-primary)"></i>
                    Student
                  </label>
                </div>
                <div class="col-6 col-md-3">
                  <input type="radio" class="btn-check" name="role" id="roleStaff" value="3" ${requestScope.roleValue == '3' ? 'checked' : ''}>
                  <label class="btn w-100 btn-outline-secondary rounded-3 py-3" for="roleStaff" style="font-size:0.82rem">
                    <i class="bi bi-person-badge-fill d-block mb-1" style="font-size:1.4rem; color:#198754"></i>
                    Staff
                  </label>
                </div>
                <div class="col-6 col-md-3">
                  <input type="radio" class="btn-check" name="role" id="roleLandlord" value="4" ${requestScope.roleValue == '4' ? 'checked' : ''}>
                  <label class="btn w-100 btn-outline-secondary rounded-3 py-3" for="roleLandlord" style="font-size:0.82rem">
                    <i class="bi bi-house-lock-fill d-block mb-1" style="font-size:1.4rem; color:#e8a020"></i>
                    Landlord
                  </label>
                </div>
                <div class="col-6 col-md-3">
                  <input type="radio" class="btn-check" name="role" id="roleAgent" value="5" ${requestScope.roleValue == '5' ? 'checked' : ''}>
                  <label class="btn w-100 btn-outline-secondary rounded-3 py-3" for="roleAgent" style="font-size:0.82rem">
                    <i class="bi bi-briefcase-fill d-block mb-1" style="font-size:1.4rem; color:#dc3545"></i>
                    Agent
                  </label>
                </div>
              </div>
            </div>

            <hr class="my-4">

            <h6 class="fw-bold mb-3" style="color:var(--re-primary)">
              <i class="bi bi-person me-2"></i>Personal Information
            </h6>

            <div class="row g-3">
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Full Name <span class="required" style="color:var(--re-danger)">*</span></label>
                  <input type="text" class="re-form-control" name="fullName"
                         placeholder="e.g. Adebayo Olusola" required
                         value="<c:out value='${requestScope.fullNameValue}'/>">
                </div>
              </div>
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Gender <span class="required" style="color:var(--re-danger)">*</span></label>
                  <select class="re-form-control" name="gender">
                    <option value="MALE" ${requestScope.genderValue == 'MALE' ? 'selected' : ''}>Male</option>
                    <option value="FEMALE" ${requestScope.genderValue == 'FEMALE' ? 'selected' : ''}>Female</option>
                    <option value="OTHER" ${requestScope.genderValue == 'OTHER' ? 'selected' : ''}>Prefer not to say</option>
                  </select>
                </div>
              </div>
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Email Address <span class="required" style="color:var(--re-danger)">*</span></label>
                  <input type="email" class="re-form-control" name="email"
                         placeholder="your@email.com" required
                         value="<c:out value='${requestScope.emailValue}'/>">
                </div>
              </div>
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Phone Number <span class="required" style="color:var(--re-danger)">*</span></label>
                  <input type="tel" class="re-form-control" name="phone"
                         placeholder="08012345678" required
                         value="<c:out value='${requestScope.phoneValue}'/>">
                </div>
              </div>
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Password <span class="required" style="color:var(--re-danger)">*</span></label>
                  <div style="position:relative">
                    <input type="password" class="re-form-control" id="regPassword"
                           name="password" placeholder="Min 8 chars, A-Z, 0-9, @#!" required>
                    <button type="button"
                            style="position:absolute; right:14px; top:50%; transform:translateY(-50%); background:none; border:none; cursor:pointer; color:var(--re-gray-500)"
                            onclick="togglePasswordVisibility('regPassword', this)"
                            aria-label="Toggle cleartext display configuration">
                      <i class="bi bi-eye"></i>
                    </button>
                  </div>
                  <div style="font-size:0.75rem; color:var(--re-gray-500); margin-top:4px">
                    Must contain uppercase, lowercase, digit, and special character.
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="re-form-group">
                  <label class="form-label">Confirm Password <span class="required" style="color:var(--re-danger)">*</span></label>
                  <input type="password" class="re-form-control" id="confirmPassword"
                         name="confirmPassword" placeholder="Repeat password" required>
                </div>
              </div>
            </div>

            <div id="studentFields" class="role-fields mt-4">
              <hr>
              <h6 class="fw-bold mb-3" style="color:var(--re-primary)">
                <i class="bi bi-mortarboard me-2"></i>Student Details
              </h6>
              <div class="row g-3">
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Matric Number</label>
                    <input type="text" class="re-form-control" name="matricNumber" 
                           placeholder="AOP/ND1/CS/2024/001" value="<c:out value='${requestScope.matricValue}'/>">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Department</label>
                    <input type="text" class="re-form-control" name="department" 
                           placeholder="Computer Science" value="<c:out value='${requestScope.deptValue}'/>">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Level</label>
                    <select class="re-form-control" name="level">
                      <option value="ND1" ${requestScope.levelValue == 'ND1' ? 'selected' : ''}>ND 1</option>
                      <option value="ND2" ${requestScope.levelValue == 'ND2' ? 'selected' : ''}>ND 2</option>
                      <option value="HND1" ${requestScope.levelValue == 'HND1' ? 'selected' : ''}>HND 1</option>
                      <option value="HND2" ${requestScope.levelValue == 'HND2' ? 'selected' : ''}>HND 2</option>
                    </select>
                  </div>
                </div>
                <div class="col-12">
                  <div class="re-form-group">
                    <label class="form-label">Faculty</label>
                    <input type="text" class="re-form-control" name="faculty" 
                           placeholder="Applied Science" value="<c:out value='${requestScope.facultyValue}'/>">
                  </div>
                </div>
              </div>
            </div>

            <div id="staffFields" class="role-fields mt-4" style="display:none">
              <hr>
              <h6 class="fw-bold mb-3" style="color:#198754">
                <i class="bi bi-person-badge me-2"></i>Staff Details
              </h6>
              <div class="row g-3">
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Staff Number</label>
                    <input type="text" class="re-form-control" name="staffNumber" 
                           placeholder="AOP/STAFF/001" value="<c:out value='${requestScope.staffNumValue}'/>">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Department</label>
                    <input type="text" class="re-form-control" name="staffDepartment" 
                           placeholder="Registry" value="<c:out value='${requestScope.deptValue}'/>">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="re-form-group">
                    <label class="form-label">Designation</label>
                    <input type="text" class="re-form-control" name="designation" 
                           placeholder="Senior Lecturer" value="<c:out value='${requestScope.designationValue}'/>">
                  </div>
                </div>
              </div>
            </div>

            <div id="landlordFields" class="role-fields mt-4" style="display:none">
              <hr>
              <h6 class="fw-bold mb-3" style="color:#e8a020">
                <i class="bi bi-house-lock me-2"></i>Landlord Details
              </h6>
              <div class="re-form-group">
                <label class="form-label">Business / Estate Name (Optional)</label>
                <input type="text" class="re-form-control" name="businessName" 
                       placeholder="Olusola Properties" value="<c:out value='${requestScope.bizNameValue}'/>">
              </div>
            </div>

            <div id="agentFields" class="role-fields mt-4" style="display:none">
              <hr>
              <h6 class="fw-bold mb-3" style="color:#dc3545">
                <i class="bi bi-briefcase me-2"></i>Agent Details
              </h6>
              <div class="re-form-group">
                <label class="form-label">Agency Name</label>
                <input type="text" class="re-form-control" name="agencyName" 
                       placeholder="Premier Properties Eruwa" value="<c:out value='${requestScope.agencyNameValue}'/>">
              </div>
            </div>

            <div class="mt-4">
              <button type="submit" class="btn btn-primary w-100 rounded-3 py-2 fw-bold" style="background-color:var(--re-primary); border-color:var(--re-primary)">
                Create Account
              </button>
            </div>
          </form>

          <div class="text-center mt-3">
            <span class="text-muted">Already have an account?</span>
            <a href="<%= contextPath %>/login" class="fw-bold ms-1 text-decoration-none" style="color:var(--re-primary)">Log In Here</a>
          </div>

        </div>
      </div>

    </div>
  </div>
</div>

<jsp:include page="/includes/footer.jsp" />

<script>
  document.querySelectorAll('input[name="role"]').forEach(radio => {
    radio.addEventListener('change', function() {
      document.querySelectorAll('.role-fields').forEach(div => div.style.display = 'none');
      if (this.value === '2') document.getElementById('studentFields').style.display = 'block';
      if (this.value === '3') document.getElementById('staffFields').style.display = 'block';
      if (this.value === '4') document.getElementById('landlordFields').style.display = 'block';
      if (this.value === '5') document.getElementById('agentFields').style.display = 'block';
    });
  });

  function togglePasswordVisibility(fieldId, button) {
    const input = document.getElementById(fieldId);
    const icon = button.querySelector('i');
    if (input.type === 'password') {
      input.type = 'text';
      icon.className = 'bi bi-eye-slash';
    } else {
      input.type = 'password';
      icon.className = 'bi bi-eye';
    }
  }
</script>
</body>
</html>