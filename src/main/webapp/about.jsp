<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% request.setAttribute("pageTitle","About"); %>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= request.getContextPath() %>">
<%@ include file="/includes/navbar.jsp" %>

<!-- Hero -->
<div class="py-5 text-center"
     style="background:linear-gradient(135deg,var(--re-primary),
            var(--re-primary-light));color:white;margin-top:0">
  <div class="container py-3">
    <h1 class="text-white mb-2">About AOPE Real Estate</h1>
    <p style="opacity:0.85;max-width:560px;margin:0 auto;font-size:1rem">
      The official accommodation discovery platform for students and staff
      of Adeseun Ogundoyin Polytechnic, Eruwa.
    </p>
  </div>
</div>

<div class="container py-5">

  <div class="row g-5 align-items-center mb-5">
    <div class="col-lg-6">
      <span class="section-eyebrow">Our Mission</span>
      <h2>Solving Accommodation Challenges at AOPE</h2>
      <p style="line-height:1.8;color:var(--re-gray-700)">
        Students and staff of Adeseun Ogundoyin Polytechnic, Eruwa have
        long struggled with the challenge of finding safe, affordable, and
        convenient accommodation near campus.
      </p>
      <p style="line-height:1.8;color:var(--re-gray-700)">
        This platform was designed and implemented as a final-year Computer
        Science project to solve this problem - providing a centralized,
        verified, and transparent accommodation marketplace for the AOP
        Eruwa community.
      </p>
    </div>
    <div class="col-lg-6">
      <div class="row g-3">
        <div class="col-6">
          <div class="p-3 rounded-3 text-center"
               style="background:rgba(25,135,84,0.08);
                      border:1px solid rgba(25,135,84,0.2)">
            <i class="bi bi-patch-check-fill"
               style="font-size:2rem;color:#198754"></i>
            <div class="fw-bold mt-2">Verified Listings</div>
            <div style="font-size:0.8rem;color:var(--re-gray-500)">
              All properties reviewed by admin
            </div>
          </div>
        </div>
        <div class="col-6">
          <div class="p-3 rounded-3 text-center"
               style="background:rgba(13,202,240,0.08);
                      border:1px solid rgba(13,202,240,0.2)">
            <i class="bi bi-shield-fill-check"
               style="font-size:2rem;color:#0dcaf0"></i>
            <div class="fw-bold mt-2">Fraud Prevention</div>
            <div style="font-size:0.8rem;color:var(--re-gray-500)">
              Report suspicious listings
            </div>
          </div>
        </div>
        <div class="col-6">
          <div class="p-3 rounded-3 text-center"
               style="background:rgba(232,160,32,0.08);
                      border:1px solid rgba(232,160,32,0.2)">
            <i class="bi bi-people-fill"
               style="font-size:2rem;color:#e8a020"></i>
            <div class="fw-bold mt-2">Roommate Matching</div>
            <div style="font-size:0.8rem;color:var(--re-gray-500)">
              Find compatible students
            </div>
          </div>
        </div>
        <div class="col-6">
          <div class="p-3 rounded-3 text-center"
               style="background:rgba(26,60,94,0.08);
                      border:1px solid rgba(26,60,94,0.2)">
            <i class="bi bi-mortarboard-fill"
               style="font-size:2rem;color:var(--re-primary)"></i>
            <div class="fw-bold mt-2">Campus Distance</div>
            <div style="font-size:0.8rem;color:var(--re-gray-500)">
              Know exactly how far from AOP
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Technology Stack -->
  <div class="p-4 rounded-3 mb-5"
       style="background:var(--re-gray-100);
              border:1px solid var(--re-gray-200)">
    <h4 class="mb-3">
      <i class="bi bi-code-slash me-2"></i>Technology Stack
    </h4>
    <div class="row g-3">
      <div class="col-md-4">
        <p class="mb-1 fw-semibold">Frontend</p>
        <ul style="font-size:0.875rem;color:var(--re-gray-700)">
          <li>JSP (JavaServer Pages)</li>
          <li>Bootstrap 5.3</li>
          <li>JavaScript / AJAX</li>
          <li>Swiper.js, Chart.js</li>
        </ul>
      </div>
      <div class="col-md-4">
        <p class="mb-1 fw-semibold">Backend</p>
        <ul style="font-size:0.875rem;color:var(--re-gray-700)">
          <li>Java Servlets (Jakarta EE)</li>
          <li>JDBC</li>
          <li>Apache Tomcat 10+</li>
          <li>BCrypt Security</li>
        </ul>
      </div>
      <div class="col-md-4">
        <p class="mb-1 fw-semibold">Database</p>
        <ul style="font-size:0.875rem;color:var(--re-gray-700)">
          <li>PostgreSQL 13+</li>
          <li>28 Relational Tables</li>
          <li>Full-text Search (GIN)</li>
          <li>Normalized Schema</li>
        </ul>
      </div>
    </div>
  </div>

  <!-- Project Info -->
  <div class="text-center">
    <h4 class="mb-3">Project Information</h4>
    <div class="row justify-content-center g-3">
      <div class="col-md-8">
        <table class="table table-bordered"
               style="font-size:0.875rem">
          <tr>
            <td class="fw-bold text-end" style="width:40%">Institution</td>
            <td>Adeseun Ogundoyin Polytechnic, Eruwa</td>
          </tr>
          <tr>
            <td class="fw-bold text-end">Department</td>
            <td>Computer Science</td>
          </tr>
          <tr>
            <td class="fw-bold text-end">Programme</td>
            <td>ND / HND Computer Science</td>
          </tr>
          <tr>
            <td class="fw-bold text-end">Project Title</td>
            <td>Design and Implementation of a Web-Based Real Estate
              Listing System</td>
          </tr>
          <tr>
            <td class="fw-bold text-end">Session</td>
            <td>2024/2025</td>
          </tr>
        </table>
      </div>
    </div>
  </div>

</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/dark-mode.js"></script>
</body>
</html>