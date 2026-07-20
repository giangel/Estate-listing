<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // REMOVED: contextPath variable to prevent duplicate local variable compilation exceptions
    String userRole    = (String)  session.getAttribute("userRole");
    String userName    = (String)  session.getAttribute("userName");
    boolean loggedIn   = (session.getAttribute("loggedInUser") != null);
%>
<nav class="navbar navbar-expand-lg re-navbar" id="mainNavbar">
  <div class="container">

    <a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">
      <div class="brand-logo">RE</div>
      <div class="brand-text">
        <span class="brand-name">AOPE Real Estate</span>
        <span class="brand-sub">Eruwa, Oyo State</span>
      </div>
    </a>

    <button class="navbar-toggler border-0" type="button"
            data-bs-toggle="collapse" data-bs-target="#navbarMain">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarMain">
      <ul class="navbar-nav mx-auto gap-1">
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/index.jsp">
            <i class="bi bi-house"></i> Home
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/properties">
            <i class="bi bi-building"></i> Browse
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/properties?featured=true">
            <i class="bi bi-star"></i> Featured
          </a>
        </li>
        <% if (loggedIn && "STUDENT".equals(userRole)) { %>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/roommate/matches">
            <i class="bi bi-people"></i> Roommates
          </a>
        </li>
        <% } %>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/about.jsp">
            <i class="bi bi-info-circle"></i> About
          </a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/contact.jsp">
            <i class="bi bi-envelope"></i> Contact
          </a>
        </li>
      </ul>

      <div class="d-flex align-items-center gap-2 mt-2 mt-lg-0">

        <button class="dark-mode-toggle" id="darkModeToggle"
                title="Toggle dark mode" type="button">
          <i class="bi bi-moon-fill" id="darkModeIcon"></i>
        </button>

        <% if (loggedIn) { %>

          <div class="notif-bell" id="notifBell"
               onclick="window.location='${pageContext.request.contextPath}/user/dashboard'">
            <i class="bi bi-bell"></i>
            <span class="notif-count" id="notifCount" style="display:none">0</span>
          </div>

          <div class="dropdown">
            <button class="btn btn-outline-brand btn-sm-custom dropdown-toggle d-flex align-items-center gap-2"
                    type="button" data-bs-toggle="dropdown">
              <i class="bi bi-person-circle"></i>
              <span class="d-none d-md-inline"><%= userName %></span>
            </button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0 rounded-3 mt-1">
              <li class="dropdown-header">
                <small class="text-muted fw-semibold"><%= userRole %></small>
              </li>
              <li><hr class="dropdown-divider my-1"></li>

              <% if ("ADMIN".equals(userRole)) { %>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard">
                  <i class="bi bi-speedometer2 me-2 text-primary"></i> Admin Dashboard
                </a>
              </li>
              <% } else if ("LANDLORD".equals(userRole)) { %>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/landlord/dashboard">
                  <i class="bi bi-speedometer2 me-2 text-primary"></i> My Dashboard
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/landlord/create-property">
                  <i class="bi bi-plus-circle me-2 text-success"></i> Add Property
                </a>
              </li>
              <% } else if ("AGENT".equals(userRole)) { %>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/agent/dashboard">
                  <i class="bi bi-speedometer2 me-2 text-primary"></i> Agent Dashboard
                </a>
              </li>
              <% } else { %>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/dashboard">
                  <i class="bi bi-speedometer2 me-2 text-primary"></i> My Dashboard
                </a>
              </li>
              <% } %>

              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/saved-properties.jsp">
                  <i class="bi bi-heart me-2 text-danger"></i> Saved Properties
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                  <i class="bi bi-person me-2 text-secondary"></i> My Profile
                </a>
              </li>
              <li><hr class="dropdown-divider my-1"></li>
              <li>
                <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">
                  <i class="bi bi-box-arrow-right me-2"></i> Logout
                </a>
              </li>
            </ul>
          </div>

        <% } else { %>
          <a href="${pageContext.request.contextPath}/login"
             class="btn btn-outline-brand btn-sm-custom">
            <i class="bi bi-box-arrow-in-right me-1"></i> Login
          </a>
<a href="${pageContext.request.contextPath}/register"
             class="btn btn-secondary-brand btn-sm-custom">
            <i class="bi bi-person-plus me-1"></i> Register
          </a>
        <% } %>
      </div>
    </div>
  </div>
  
  <style>
@media (max-width: 991px) {
  #navbarMain.navbar-collapse.show,
  #navbarMain.navbar-collapse.collapsing {
    background-color: #ffffff !important;
    opacity: 1 !important;
    position: absolute !important;
    top: 100% !important;
    left: 0 !important;
    right: 0 !important;
    z-index: 2000 !important;
    box-shadow: 0 4px 20px rgba(0,0,0,0.25) !important;
    padding: 1rem 1.5rem !important;
    max-height: calc(100vh - 72px) !important;
    overflow-y: auto !important;
  }
}
</style>
</nav>