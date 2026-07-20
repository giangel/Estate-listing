<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% request.setAttribute("pageTitle","Contact Us"); %>
<!DOCTYPE html>
<html lang="en">
<head><%@ include file="/includes/head-meta.jsp" %></head>
<body data-ctx="<%= request.getContextPath() %>">
<%@ include file="/includes/navbar.jsp" %>

<div class="container py-5">

  <div class="row g-5">

    <!-- Contact Info -->
    <div class="col-lg-4">
      <h2 class="mb-4">Get In Touch</h2>
      <p class="text-muted mb-4">
        Have a question about a listing, need help with your account,
        or want to report an issue? Contact us below.
      </p>

      <div class="d-flex flex-column gap-3">
        <div class="d-flex align-items-center gap-3">
          <div style="width:48px;height:48px;border-radius:12px;
                      background:rgba(26,60,94,0.1);display:flex;
                      align-items:center;justify-content:center;flex-shrink:0">
            <i class="bi bi-geo-alt-fill"
               style="font-size:1.2rem;color:var(--re-primary)"></i>
          </div>
          <div>
            <div class="fw-semibold">Address</div>
            <div style="font-size:0.875rem;color:var(--re-gray-500)">
              Adeseun Ogundoyin Polytechnic,<br>
              Eruwa, Ibarapa East LGA, Oyo State
            </div>
          </div>
        </div>

        <div class="d-flex align-items-center gap-3">
          <div style="width:48px;height:48px;border-radius:12px;
                      background:rgba(25,135,84,0.1);display:flex;
                      align-items:center;justify-content:center;flex-shrink:0">
            <i class="bi bi-envelope-fill"
               style="font-size:1.2rem;color:#198754"></i>
          </div>
          <div>
            <div class="fw-semibold">Email</div>
            <div style="font-size:0.875rem;color:var(--re-gray-500)">
              admin@aoprealestate.edu.ng
            </div>
          </div>
        </div>

        <div class="d-flex align-items-center gap-3">
          <div style="width:48px;height:48px;border-radius:12px;
                      background:rgba(232,160,32,0.1);display:flex;
                      align-items:center;justify-content:center;flex-shrink:0">
            <i class="bi bi-clock-fill"
               style="font-size:1.2rem;color:var(--re-secondary)"></i>
          </div>
          <div>
            <div class="fw-semibold">Support Hours</div>
            <div style="font-size:0.875rem;color:var(--re-gray-500)">
              Monday – Friday: 8am – 5pm<br>
              Saturday: 9am – 1pm
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Contact Form -->
    <div class="col-lg-8">
      <div class="card border-0 shadow-sm rounded-4 p-4">
        <h4 class="mb-4">Send Us a Message</h4>

        <c:if test="${not empty requestScope.successMessage}">
          <div class="re-alert re-alert-success">
            <i class="bi bi-check-circle-fill"></i>
            <c:out value="${requestScope.successMessage}"/>
          </div>
        </c:if>

        <%--
          NOTE: This form is a UI demonstration only.
          In production, connect it to a ContactServlet that sends
          the message to the admin email via EmailUtil.
        --%>
        <form onsubmit="handleContactForm(event)">
          <div class="row g-3">
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Your Name <span class="required">*</span></label>
                <input type="text" class="re-form-control"
                       id="contactName" placeholder="Adewale Okafor"
                       required>
              </div>
            </div>
            <div class="col-md-6">
              <div class="re-form-group">
                <label>Email Address <span class="required">*</span></label>
                <input type="email" class="re-form-control"
                       id="contactEmail" placeholder="your@email.com"
                       required>
              </div>
            </div>
            <div class="col-12">
              <div class="re-form-group">
                <label>Subject <span class="required">*</span></label>
                <select class="re-form-control" id="contactSubject">
                  <option value="">Select subject</option>
                  <option>Listing Enquiry</option>
                  <option>Account Issue</option>
                  <option>Report Fraud</option>
                  <option>General Question</option>
                  <option>Technical Problem</option>
                </select>
              </div>
            </div>
            <div class="col-12">
              <div class="re-form-group">
                <label>Message <span class="required">*</span></label>
                <textarea class="re-form-control" id="contactMessage"
                          rows="5"
                          placeholder="Type your message here..."
                          required></textarea>
              </div>
            </div>
          </div>
          <button type="submit" class="btn btn-primary-brand btn-lg-custom">
            <i class="bi bi-send me-2"></i> Send Message
          </button>
        </form>
      </div>
    </div>

  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/dark-mode.js"></script>
<script>
function handleContactForm(e) {
  e.preventDefault();
  // Simulate form submission success
  var name = document.getElementById('contactName').value;
  showToast('Thank you, ' + name +
    '! Your message has been sent. We will respond within 24 hours.',
    'success');
  e.target.reset();
}
</script>
</body>
</html>