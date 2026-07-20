<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
</head>
<body data-ctx="<%= ctx %>">
<jsp:include page="/includes/navbar.jsp" />

<div class="container py-5">

  <!-- Page Header -->
  <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-4">
    <div>
      <h2><i class="bi bi-people-fill me-2"></i>Find a Roommate</h2>
      <p class="text-muted mb-0">
        Discover compatible AOP students to share accommodation costs.
      </p>
    </div>
    <a href="<%= ctx %>/roommate/profile" class="btn btn-primary-brand btn-sm-custom">
      <i class="bi bi-person-gear me-1"></i>
      <c:choose>
        <c:when test="${not empty myProfile}">Edit My Profile</c:when>
        <c:otherwise>Create Profile</c:otherwise>
      </c:choose>
    </a>
  </div>

  <%@ include file="/includes/alerts.jsp" %>

  <!-- Profile Status Banner Framework -->
  <c:choose>
    <c:when test="${empty myProfile}">
      <div class="re-alert re-alert-info mb-4">
        <i class="bi bi-info-circle-fill"></i>
        <div>
          <strong>Create your roommate profile first!</strong>
          Set your preferences so we can find compatible matches for you.
          <a href="<%= ctx %>/roommate/profile" style="color:var(--re-primary); font-weight:600; margin-left:4px">
            Create Profile →
          </a>
        </div>
      </div>
    </c:when>
    <c:otherwise>
      <!-- Active Profile Configured Summary Panel -->
      <div class="p-3 rounded-3 mb-4" style="background:rgba(26,60,94,0.05); border:1px solid rgba(26,60,94,0.15)">
        <div class="row align-items-center">
          <div class="col-md-8">
            <h6 class="mb-2">Your Roommate Profile</h6>
            <div class="d-flex flex-wrap gap-2">
              <span class="re-badge re-badge-distance">
                <i class="bi bi-gender-ambiguous me-1"></i>
                <c:out value="${myProfile.genderPreference}"/> Preferred
              </span>
              <span class="re-badge re-badge-distance">
                <i class="bi bi-currency-naira me-1"></i>
                <c:out value="${myProfile.budgetRangeDisplay}"/>
              </span>
              <c:if test="${not empty myProfile.level}">
                <span class="re-badge re-badge-verified">
                  <i class="bi bi-mortarboard me-1"></i>
                  <c:out value="${myProfile.level}"/>
                </span>
              </c:if>
              <c:if test="${not empty myProfile.preferredArea}">
                <span class="re-badge re-badge-distance">
                  <i class="bi bi-geo-alt me-1"></i>
                  <c:out value="${myProfile.preferredArea}"/>
                </span>
              </c:if>
            </div>
          </div>
          <div class="col-md-4 text-md-end mt-3 mt-md-0">
            <span class="re-badge ${myProfile.active ? 're-badge-available' : 're-badge-occupied'}">
              <c:out value="${myProfile.active ? '● Active' : '● Inactive'}"/>
            </span>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <!-- Match Results Grid Matrix Evaluation -->
  <c:choose>
    <c:when test="${not empty matches}">
      <h5 class="mb-3">
        <i class="bi bi-stars me-2 text-warning"></i>
        <c:out value="${fn:length(matches)}"/> Compatible Roommates Found
      </h5>
      
      <div class="row g-4">
        <c:forEach var="match" items="${matches}">
          <div class="col-lg-4 col-md-6">
            <div class="card border-0 shadow-sm rounded-4 h-100">
              <div class="card-body p-4">

                <!-- Profile Identity Block -->
                <div class="d-flex align-items-center gap-3 mb-3">
                  
                  <!-- Safe Fallback Initial Parsing Segment -->
                  <div style="width:56px; height:56px; border-radius:50%; background:linear-gradient(135deg, var(--re-primary), var(--re-primary-light)); display:flex; align-items:center; justify-content:center; color:white; font-size:1.3rem; font-weight:700; flex-shrink:0">
                    <c:choose>
                      <c:when test="${not empty match.user and not empty match.user.fullName}">
                        <c:out value="${fn:toUpperCase(fn:substring(match.user.fullName, 0, 1))}"/>
                      </c:when>
                      <c:otherwise>S</c:otherwise>
                    </c:choose>
                  </div>
                  
                  <div>
                    <div class="fw-bold text-truncate" style="max-width:160px;">
                      <c:choose>
                        <c:when test="${not empty match.user and not empty match.user.fullName}">
                          <c:out value="${match.user.fullName}"/>
                        </c:when>
                        <c:otherwise>AOP Student</c:otherwise>
                      </c:choose>
                    </div>
                    <div style="font-size:0.78rem; color:var(--re-gray-500)">
                      AOPE
                    </div>
                  </div>
                  
                  <!-- Match System Matrix Score Display Badge -->
                  <div class="ms-auto">
                    <div style="width:50px; height:50px; border-radius:50%; 
                                background:${match.matchScore >= 75 ? 'rgba(25,135,84,0.15)' : match.matchScore >= 50 ? 'rgba(255,193,7,0.15)' : 'rgba(220,53,69,0.1)'};
                                display:flex; align-items:center; justify-content:center; flex-direction:column; 
                                border:2px solid ${match.matchScore >= 75 ? '#198754' : match.matchScore >= 50 ? '#ffc107' : '#dc3545'}">
                      <span style="font-size:0.9rem; font-weight:800; line-height:1; color:${match.matchScore >= 75 ? '#198754' : match.matchScore >= 50 ? '#664d03' : '#7a1020'}">
                        <c:out value="${match.matchScore}"/>
                      </span>
                      <span style="font-size:0.55rem; font-weight:600; color:var(--re-gray-500)">MATCH</span>
                    </div>
                  </div>
                </div>

                <!-- Attributes and Sub-details Context -->
                <div class="d-flex flex-column gap-2 mb-3">
                  <c:if test="${not empty match.level}">
                    <div class="d-flex align-items-center gap-2" style="font-size:0.82rem">
                      <i class="bi bi-mortarboard" style="color:var(--re-primary); width:16px"></i>
                      <c:out value="${match.level}"/>
                      <c:if test="${not empty match.department}">
                        - <c:out value="${match.department}"/>
                      </c:if>
                    </div>
                  </c:if>
                  <div class="d-flex align-items-center gap-2" style="font-size:0.82rem">
                    <i class="bi bi-currency-naira" style="color:var(--re-secondary); width:16px"></i>
                    <c:out value="${match.budgetRangeDisplay}"/>
                  </div>
                  <c:if test="${not empty match.preferredArea}">
                    <div class="d-flex align-items-center gap-2" style="font-size:0.82rem">
                      <i class="bi bi-geo-alt" style="color:var(--re-danger); width:16px"></i>
                      <c:out value="${match.preferredArea}"/>
                    </div>
                  </c:if>
                  <div class="d-flex align-items-center gap-2" style="font-size:0.82rem">
                    <i class="bi bi-gender-ambiguous" style="color:#6610f2; width:16px"></i>
                    Prefers: <c:out value="${match.genderPreference}"/>
                  </div>
                </div>

                <!-- Bio / Personal Statement Paragraph Snippet -->
                <c:if test="${not empty match.description}">
                  <p style="font-size:0.8rem; color:var(--re-gray-700); border-top:1px solid var(--re-gray-200); padding-top:0.75rem; margin-bottom:1rem; font-style:italic">
                    "<c:out value="${match.description}"/>"
                  </p>
                </c:if>

                <!-- Outbound Connection Intersect Request Processing Interface -->
                <form action="<%= ctx %>/roommate/profile" method="POST">
                  <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                  <input type="hidden" name="action" value="request">
                  <input type="hidden" name="receiverId" value="<c:out value='${match.userId}'/>">
                  
                  <div class="re-form-group" style="margin-bottom:0.5rem">
                    <input type="text" class="re-form-control" name="message" 
                           placeholder="Hi! I'm looking for a roommate..." style="height:36px; font-size:0.8rem" required>
                  </div>
                  <button type="submit" class="btn btn-primary-brand w-100" style="padding:0.5rem">
                    <i class="bi bi-send me-1"></i> Send Connection Request
                  </button>
                </form>

              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </c:when>
    <c:when test="${empty myProfile}">
      <%-- Handled conditionally in header view block above --%>
    </c:when>
    <c:otherwise>
      <!-- Structured Empty Display State Map Context -->
      <div class="empty-state text-center py-5">
        <div class="empty-icon mb-3" style="font-size:3rem; color:var(--re-gray-400)">
          <i class="bi bi-people"></i>
        </div>
        <h4>No Matches Found Yet</h4>
        <p class="text-muted mx-auto" style="max-width:420px;">
          No compatible roommate profiles match your preferences yet. Check back as more students create profiles!
        </p>
        <a href="<%= ctx %>/roommate/profile" class="btn btn-outline-brand mt-2">
          <i class="bi bi-pencil me-1"></i> Update My Preferences
        </a>
      </div>
    </c:otherwise>
  </c:choose>

</div>

<jsp:include page="/includes/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
</body>
</html>