<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String ctx    = request.getContextPath();
    int    userId = (Integer) session.getAttribute("userId");

    // Load agent record
    java.sql.Connection conn = null;
    com.realestate.model.Agent agentRecord = null;
    try {
        conn = com.realestate.util.DBConnection.getConnection();
        java.sql.PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM agents WHERE user_id = ?");
        ps.setInt(1, userId);
        java.sql.ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            agentRecord = new com.realestate.model.Agent();
            agentRecord.setAgentId(rs.getInt("agent_id"));
            agentRecord.setAgencyName(rs.getString("agency_name"));
            agentRecord.setLicenseNumber(rs.getString("license_number"));
            agentRecord.setVerificationStatus(rs.getString("verification_status"));
            agentRecord.setVerifiedAt(rs.getTimestamp("verified_at"));
        }
        rs.close(); ps.close();
    } catch (Exception e) {
        System.err.println("[verification-status.jsp] " + e.getMessage());
    } finally {
        com.realestate.util.DBConnection.closeConnection(conn);
    }
    request.setAttribute("agentRecord", agentRecord);
%>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= ctx %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-6">

      <h2 class="mb-4">
        <i class="bi bi-patch-check me-2"
           style="color:var(--re-secondary)"></i>
        Agent Verification Status
      </h2>

      <c:if test="${not empty agentRecord}">
        <div class="card border-0 shadow-sm rounded-4 p-4">

          <div class="text-center mb-4">
            <c:choose>
              <c:when test="${agentRecord.verificationStatus == 'VERIFIED'}">
                <i class="bi bi-patch-check-fill"
                   style="font-size:4rem;color:#198754"></i>
                <h4 class="mt-2 text-success">Account Verified</h4>
                <p class="text-muted">
                  Your agent account has been verified.
                  You can now manage properties.
                </p>
              </c:when>
              <c:when test="${agentRecord.verificationStatus == 'REJECTED'}">
                <i class="bi bi-patch-exclamation-fill"
                   style="font-size:4rem;color:#dc3545"></i>
                <h4 class="mt-2 text-danger">Verification Rejected</h4>
                <p class="text-muted">
                  Your verification was not approved.
                  Please contact the administrator.
                </p>
              </c:when>
              <c:otherwise>
                <i class="bi bi-hourglass-split"
                   style="font-size:4rem;color:#fd7e14"></i>
                <h4 class="mt-2" style="color:#fd7e14">
                  Pending Verification
                </h4>
                <p class="text-muted">
                  Your account is under review by the administrator.
                  Please check back soon.
                </p>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="p-3 rounded-3"
               style="background:var(--re-gray-100);
                      border:1px solid var(--re-gray-200)">
            <table style="width:100%;font-size:0.875rem">
              <tr>
                <td style="color:var(--re-gray-500);padding:4px 0">
                  Agency Name:
                </td>
                <td style="font-weight:600">
                  <c:out value="${not empty agentRecord.agencyName
                                 ? agentRecord.agencyName : 'Not provided'}"/>
                </td>
              </tr>
              <tr>
                <td style="color:var(--re-gray-500);padding:4px 0">
                  License No:
                </td>
                <td style="font-weight:600">
                  <c:out value="${not empty agentRecord.licenseNumber
                                 ? agentRecord.licenseNumber
                                 : 'Not provided'}"/>
                </td>
              </tr>
              <tr>
                <td style="color:var(--re-gray-500);padding:4px 0">
                  Status:
                </td>
                <td>
                  <span class="re-badge
                    ${agentRecord.verificationStatus == 'VERIFIED'
                        ? 're-badge-available' :
                      agentRecord.verificationStatus == 'REJECTED'
                        ? 're-badge-occupied' :
                        're-badge-pending'}">
                    <c:out value="${agentRecord.verificationStatus}"/>
                  </span>
                </td>
              </tr>
              <c:if test="${not empty agentRecord.verifiedAt}">
                <tr>
                  <td style="color:var(--re-gray-500);padding:4px 0">
                    Verified On:
                  </td>
                  <td style="font-weight:600">
                    <fmt:formatDate
                        xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
                        value="${agentRecord.verifiedAt}"
                        pattern="dd MMM yyyy"/>
                  </td>
                </tr>
              </c:if>
            </table>
          </div>

          <a href="<%= ctx %>/agent/agent-dashboard.jsp"
             class="btn btn-outline-brand w-100 mt-3">
            <i class="bi bi-arrow-left me-1"></i> Back to Dashboard
          </a>

        </div>
      </c:if>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= ctx %>/assets/js/dark-mode.js"></script>
</body>
</html>