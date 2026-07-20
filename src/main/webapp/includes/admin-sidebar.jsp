<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
    String userName    = (String) session.getAttribute("userName");
    String initials    = (userName != null && userName.length() > 0)
                         ? String.valueOf(userName.charAt(0)).toUpperCase() : "A";
%>
<aside class="re-sidebar" id="dashSidebar">
  <div class="re-sidebar-header">
    <div class="re-sidebar-avatar"><%= initials %></div>
    <div>
      <div class="re-sidebar-user-name"><%= userName %></div>
      <div class="re-sidebar-user-role">Administrator</div>
    </div>
  </div>

  <nav class="re-sidebar-nav">
    <div class="re-sidebar-section-title">Overview</div>

    <a href="<%= contextPath %>/admin/dashboard"
       class="re-sidebar-link <%= "admin/dashboard".equals(request.getAttribute("activePage")) ? "active" : "" %>">
      <i class="bi bi-speedometer2"></i> Dashboard
    </a>

    <div class="re-sidebar-section-title">Property Management</div>

    <a href="<%= contextPath %>/admin/manage-properties"
       class="re-sidebar-link">
      <i class="bi bi-building"></i> All Properties
    </a>
    <a href="<%= contextPath %>/admin/manage-properties?status=PENDING"
       class="re-sidebar-link">
      <i class="bi bi-hourglass-split"></i> Pending Approval
      <span class="sidebar-badge" id="pendingCount">0</span>
    </a>
    <a href="<%= contextPath %>/admin/verify-agent"
       class="re-sidebar-link">
      <i class="bi bi-patch-check"></i> Verify Agents
    </a>

    <div class="re-sidebar-section-title">User Management</div>

    <a href="<%= contextPath %>/admin/users"
       class="re-sidebar-link">
      <i class="bi bi-people"></i> All Users
    </a>
    <a href="<%= contextPath %>/admin/users?role=STUDENT"
       class="re-sidebar-link">
      <i class="bi bi-mortarboard"></i> Students
    </a>
    <a href="<%= contextPath %>/admin/users?role=LANDLORD"
       class="re-sidebar-link">
      <i class="bi bi-house-lock"></i> Landlords
    </a>

    <div class="re-sidebar-section-title">Monitoring</div>

    <a href="<%= contextPath %>/admin/fraud"
       class="re-sidebar-link">
      <i class="bi bi-shield-exclamation"></i> Fraud Reports
      <span class="sidebar-badge" id="fraudCount">0</span>
    </a>
    <a href="<%= contextPath %>/admin/reports"
       class="re-sidebar-link">
      <i class="bi bi-bar-chart-line"></i> Reports
    </a>
    <a href="<%= contextPath %>/admin/audit-logs.jsp"
       class="re-sidebar-link">
      <i class="bi bi-journal-text"></i> Audit Logs
    </a>

    <div class="re-sidebar-section-title">System</div>

    <a href="<%= contextPath %>/index.jsp" class="re-sidebar-link">
      <i class="bi bi-globe"></i> View Website
    </a>
    <a href="<%= contextPath %>/logout" class="re-sidebar-link"
       onclick="return confirm('Confirm logout?')">
      <i class="bi bi-box-arrow-right"></i> Logout
    </a>
  </nav>
</aside>
<div class="sidebar-overlay" id="sidebarOverlay"></div>