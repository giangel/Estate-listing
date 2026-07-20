<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="/includes/head-meta.jsp" %>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/dashboard.css">
</head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="re-dashboard">
  <jsp:include page="/includes/admin-sidebar.jsp" />
  <main class="re-dashboard-content">

    <div class="dashboard-page-header">
      <div>
        <h2><i class="bi bi-people me-2"></i>User Management</h2>
        <p class="text-muted mb-0">
          Total: <strong>${totalCount}</strong> users registered.
        </p>
      </div>
      <!-- Search -->
      <form action="<%= ctx %>/admin/users" method="GET"
            class="d-flex gap-2">
        <input type="text" class="re-form-control"
               name="q" placeholder="Search by name or email..."
               value="<c:out value='${searchKeyword}'/>"
               style="height:38px;width:220px">
        <button type="submit" class="btn btn-primary-brand"
                style="padding:0 1rem;height:38px">
          <i class="bi bi-search"></i>
        </button>
      </form>
    </div>

    <%@ include file="/includes/alerts.jsp" %>

    <!-- Role Filter Tabs -->
    <div class="d-flex gap-2 mb-3 flex-wrap">
      <a href="<%= ctx %>/admin/users"
         class="btn btn-sm ${empty selectedRole ? 'btn-primary-brand' : 'btn-outline-secondary'}">
        All (${totalCount})
      </a>
      <a href="<%= ctx %>/admin/users?role=STUDENT"
         class="btn btn-sm ${selectedRole == 'STUDENT' ? 'btn-primary-brand' : 'btn-outline-secondary'}">
        Students (${studentCount})
      </a>
      <a href="<%= ctx %>/admin/users?role=STAFF"
         class="btn btn-sm ${selectedRole == 'STAFF' ? 'btn-primary-brand' : 'btn-outline-secondary'}">
        Staff (${staffCount})
      </a>
      <a href="<%= ctx %>/admin/users?role=LANDLORD"
         class="btn btn-sm ${selectedRole == 'LANDLORD' ? 'btn-primary-brand' : 'btn-outline-secondary'}">
        Landlords (${landlordCount})
      </a>
      <a href="<%= ctx %>/admin/users?role=AGENT"
         class="btn btn-sm ${selectedRole == 'AGENT' ? 'btn-primary-brand' : 'btn-outline-secondary'}">
        Agents (${agentCount})
      </a>
    </div>

    <div class="re-table-wrapper">
      <div class="table-responsive">
        <table class="re-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Role</th>
              <th>Status</th>
              <th>Joined</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty users}">
                <c:forEach var="u" items="${users}">
                  <tr>
                    <td style="font-size:0.8rem;color:var(--re-gray-500)">
                      ${u.userId}
                    </td>
                    <td>
                      <div class="d-flex align-items-center gap-2">
                        <div style="width:34px;height:34px;border-radius:50%;
                                    background:var(--re-primary);display:flex;
                                    align-items:center;justify-content:center;
                                    color:white;font-weight:700;
                                    font-size:0.85rem;flex-shrink:0">
                          ${u.initials}
                        </div>
                        <div>
                          <div style="font-weight:600;font-size:0.875rem">
                            <c:out value="${u.fullName}"/>
                          </div>
                          <div style="font-size:0.72rem;color:var(--re-gray-500)">
                            ${u.verified ? '✓ Verified' : 'Unverified'}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${u.email}"/>
                    </td>
                    <td style="font-size:0.82rem">
                      <c:out value="${u.phone}"/>
                    </td>
                    <td>
                      <span class="re-badge re-badge-distance"
                            style="font-size:0.72rem">
                        <c:out value="${u.roleName}"/>
                      </span>
                    </td>
                    <td>
                      <span class="re-badge
                        ${u.accountStatus == 'ACTIVE'
                            ? 're-badge-available' :
                          u.accountStatus == 'SUSPENDED'
                            ? 're-badge-pending' :
                            're-badge-occupied'}"
                            style="font-size:0.72rem">
                        <c:out value="${u.accountStatus}"/>
                      </span>
                    </td>
                    <td style="font-size:0.78rem;color:var(--re-gray-500)">
                      <fmt:formatDate value="${u.createdAt}"
                                      pattern="dd/MM/yy"/>
                    </td>
                    <td>
                      <div class="dropdown">
                        <button class="btn btn-sm btn-outline-secondary
                                       dropdown-toggle"
                                type="button"
                                data-bs-toggle="dropdown"
                                style="padding:4px 10px;font-size:0.78rem">
                          Actions
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow
                                   border-0 rounded-3">
                          <c:if test="${u.accountStatus != 'ACTIVE'}">
                            <li>
                              <form action="<%= ctx %>/admin/users"
                                    method="POST">
                                <input type="hidden" name="_csrf"
                                       value="${sessionScope.csrfToken}">
                                <input type="hidden" name="userId"
                                       value="${u.userId}">
                                <input type="hidden" name="status"
                                       value="ACTIVE">
                                <button type="submit"
                                        class="dropdown-item text-success">
                                  <i class="bi bi-check-circle me-2"></i>
                                  Activate
                                </button>
                              </form>
                            </li>
                          </c:if>
                          <c:if test="${u.accountStatus != 'SUSPENDED'}">
                            <li>
                              <form action="<%= ctx %>/admin/users"
                                    method="POST">
                                <input type="hidden" name="_csrf"
                                       value="${sessionScope.csrfToken}">
                                <input type="hidden" name="userId"
                                       value="${u.userId}">
                                <input type="hidden" name="status"
                                       value="SUSPENDED">
                                <button type="submit"
                                        class="dropdown-item text-warning"
                                        onclick="return confirm(
                                          'Suspend this account?')">
                                  <i class="bi bi-pause-circle me-2"></i>
                                  Suspend
                                </button>
                              </form>
                            </li>
                          </c:if>
                          <c:if test="${u.accountStatus != 'BLACKLISTED'}">
                            <li>
                              <form action="<%= ctx %>/admin/users"
                                    method="POST">
                                <input type="hidden" name="_csrf"
                                       value="${sessionScope.csrfToken}">
                                <input type="hidden" name="userId"
                                       value="${u.userId}">
                                <input type="hidden" name="status"
                                       value="BLACKLISTED">
                                <button type="submit"
                                        class="dropdown-item text-danger"
                                        onclick="return confirm(
                                          'Blacklist this account?')">
                                  <i class="bi bi-ban me-2"></i>
                                  Blacklist
                                </button>
                              </form>
                            </li>
                          </c:if>
                        </ul>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="8" class="text-center py-4 text-muted">
                    No users found.
                  </td>
                </tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>

  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
<script src="<%= ctx %>/assets/js/main.js"></script>
</body>
</html>