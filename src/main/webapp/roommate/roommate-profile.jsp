<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-4">
  <div class="row g-4">

    <!-- Profile Form -->
    <div class="col-lg-7">
      <div class="card border-0 shadow-sm rounded-4 p-4">
        <h4 class="mb-1">
          <i class="bi bi-person-badge me-2"
             style="color:var(--re-primary)"></i>
          My Roommate Profile
        </h4>
        <p class="text-muted mb-4" style="font-size:0.875rem">
          Set your preferences to get matched with compatible roommates.
        </p>

        <%@ include file="/includes/alerts.jsp" %>

        <form action="<%= ctx %>/roommate/profile" method="POST">
          <input type="hidden" name="_csrf"
                 value="${sessionScope.csrfToken}">

          <div class="row g-3">
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Gender Preference</label>
                <select class="re-form-control" name="genderPreference">
                  <option value="ANY"
                    <c:if test="${empty roommateProfile or roommateProfile.genderPreference == 'ANY'}">
                      selected
                    </c:if>>Any Gender</option>
                  <option value="MALE"
                    <c:if test="${roommateProfile.genderPreference == 'MALE'}">
                      selected
                    </c:if>>Male Only</option>
                  <option value="FEMALE"
                    <c:if test="${roommateProfile.genderPreference == 'FEMALE'}">
                      selected
                    </c:if>>Female Only</option>
                </select>
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Your Level</label>
                <select class="re-form-control" name="level">
                  <c:forEach var="lvl" items="ND1,ND2,HND1,HND2">
                    <option value="${lvl}"
                      <c:if test="${roommateProfile.level == lvl}">selected</c:if>>
                      <c:out value="${lvl}"/>
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Budget Min (₦)</label>
                <input type="number" class="re-form-control"
                       name="budgetMin" min="0" step="5000"
                       placeholder="50000"
                       value="${roommateProfile.budgetMin}">
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Budget Max (₦)</label>
                <input type="number" class="re-form-control"
                       name="budgetMax" min="0" step="5000"
                       placeholder="150000"
                       value="${roommateProfile.budgetMax}">
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Preferred Area</label>
                <input type="text" class="re-form-control"
                       name="preferredArea"
                       placeholder="e.g. Eruwa Town Centre"
                       value="<c:out value='${roommateProfile.preferredArea}'/>">
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Your Department</label>
                <input type="text" class="re-form-control"
                       name="department"
                       placeholder="e.g. Computer Science"
                       value="<c:out value='${roommateProfile.department}'/>">
              </div>
            </div>
            <div class="col-12">
              <div class="re-form-group">
                <label>About You (Optional)</label>
                <textarea class="re-form-control" name="description"
                          rows="3"
                          placeholder="Tell potential roommates about yourself, your habits, and what you are looking for..."><c:out value="${roommateProfile.description}"/></textarea>
              </div>
            </div>
          </div>

          <div class="d-flex gap-3 mt-2">
            <button type="submit" class="btn btn-primary-brand">
              <i class="bi bi-check-circle me-1"></i>
              ${not empty roommateProfile ? 'Update Profile' : 'Create Profile'}
            </button>
            <a href="<%= ctx %>/roommate/matches"
               class="btn btn-outline-brand">
              <i class="bi bi-people me-1"></i> View Matches
            </a>
          </div>

        </form>
      </div>
    </div>

    <!-- Incoming Requests -->
    <div class="col-lg-5">
      <div class="card border-0 shadow-sm rounded-4 p-4">
        <h5 class="mb-3">
          <i class="bi bi-inbox me-2" style="color:var(--re-secondary)"></i>
          Incoming Requests
          <c:if test="${not empty incomingRequests}">
            <span class="badge ms-1"
                  style="background:var(--re-secondary)">
              ${incomingRequests.size()}
            </span>
          </c:if>
        </h5>

        <c:choose>
          <c:when test="${not empty incomingRequests}">
            <c:forEach var="req" items="${incomingRequests}">
              <div class="p-3 mb-3 rounded-3"
                   style="background:rgba(26,60,94,0.04);
                          border:1px solid rgba(26,60,94,0.1)">
                <div class="d-flex align-items-center gap-2 mb-2">
                  <div style="width:38px;height:38px;border-radius:50%;
                              background:var(--re-primary);display:flex;
                              align-items:center;justify-content:center;
                              color:white;font-weight:700;font-size:0.9rem">
                    ${req.senderName.substring(0,1).toUpperCase()}
                  </div>
                  <div>
                    <div style="font-weight:600;font-size:0.875rem">
                      <c:out value="${req.senderName}"/>
                    </div>
                    <div style="font-size:0.75rem;color:var(--re-gray-500)">
                      <c:out value="${req.senderDepartment}"/>
                      <c:if test="${not empty req.senderLevel}">
                        - <c:out value="${req.senderLevel}"/>
                      </c:if>
                    </div>
                  </div>
                </div>
                <c:if test="${not empty req.message}">
                  <p style="font-size:0.8rem;color:var(--re-gray-700);
                             font-style:italic;margin-bottom:0.75rem">
                    "<c:out value="${req.message}"/>"
                  </p>
                </c:if>
                <div class="d-flex gap-2">
                  <form action="<%= ctx %>/roommate/profile"
                        method="POST" style="flex:1">
                    <input type="hidden" name="_csrf"
                           value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="respond">
                    <input type="hidden" name="requestId"
                           value="${req.requestId}">
                    <input type="hidden" name="status" value="ACCEPTED">
                    <button type="submit"
                            class="btn w-100"
                            style="background:#198754;color:white;
                                   font-size:0.8rem;padding:6px">
                      <i class="bi bi-check"></i> Accept
                    </button>
                  </form>
                  <form action="<%= ctx %>/roommate/profile"
                        method="POST" style="flex:1">
                    <input type="hidden" name="_csrf"
                           value="${sessionScope.csrfToken}">
                    <input type="hidden" name="action" value="respond">
                    <input type="hidden" name="requestId"
                           value="${req.requestId}">
                    <input type="hidden" name="status" value="DECLINED">
                    <button type="submit"
                            class="btn w-100"
                            style="background:#dc3545;color:white;
                                   font-size:0.8rem;padding:6px">
                      <i class="bi bi-x"></i> Decline
                    </button>
                  </form>
                </div>
              </div>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <div class="text-center py-3 text-muted">
              <i class="bi bi-inbox"
                 style="font-size:2rem;opacity:0.3"></i>
              <p class="mt-2 mb-0" style="font-size:0.875rem">
                No pending requests yet.
              </p>
            </div>
          </c:otherwise>
        </c:choose>

      </div>
    </div>

  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>