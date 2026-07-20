<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // REMOVED: contextPath variable to prevent duplicate local variable compilation exceptions
    int currentYear    = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
%>
<footer class="re-footer">
  <div class="container">
    <div class="row g-5">

      <div class="col-lg-4 col-md-6">
        <div class="footer-brand">
          <div class="footer-logo">RE</div>
          <div>
            <div class="footer-brand-name">AOPE Real Estate</div>
            <div class="footer-brand-sub">Adeseun Ogundoyin Polytechnic</div>
          </div>
        </div>
        <p class="footer-desc">
          The official accommodation discovery platform for students and staff
          of Adeseun Ogundoyin Polytechnic, Eruwa. Find verified, affordable
          housing near campus.
        </p>
        <div class="social-links">
          <a href="#" class="social-link" title="Facebook"><i class="bi bi-facebook"></i></a>
          <a href="#" class="social-link" title="Twitter"><i class="bi bi-twitter-x"></i></a>
          <a href="#" class="social-link" title="Instagram"><i class="bi bi-instagram"></i></a>
          <a href="#" class="social-link" title="WhatsApp"><i class="bi bi-whatsapp"></i></a>
        </div>
      </div>

      <div class="col-lg-2 col-md-6 col-6">
        <h6 class="footer-heading">Quick Links</h6>
        <ul class="footer-links">
          <li><a href="${pageContext.request.contextPath}/index.jsp">
            <i class="bi bi-chevron-right"></i> Home</a></li>
          <li><a href="${pageContext.request.contextPath}/properties">
            <i class="bi bi-chevron-right"></i> Browse Properties</a></li>
          <li><a href="${pageContext.request.contextPath}/properties?featured=true">
            <i class="bi bi-chevron-right"></i> Featured Listings</a></li>
          <li><a href="${pageContext.request.contextPath}/about.jsp">
            <i class="bi bi-chevron-right"></i> About Us</a></li>
          <li><a href="${pageContext.request.contextPath}/contact.jsp">
            <i class="bi bi-chevron-right"></i> Contact</a></li>
        </ul>
      </div>

      <div class="col-lg-2 col-md-6 col-6">
        <h6 class="footer-heading">Property Types</h6>
        <ul class="footer-links">
          <li><a href="${pageContext.request.contextPath}/properties?type=1">
            <i class="bi bi-chevron-right"></i> Self-Contain</a></li>
          <li><a href="${pageContext.request.contextPath}/properties?type=2">
            <i class="bi bi-chevron-right"></i> Single Room</a></li>
          <li><a href="${pageContext.request.contextPath}/properties?type=3">
            <i class="bi bi-chevron-right"></i> Mini Flat</a></li>
          <li><a href="${pageContext.request.contextPath}/properties?type=4">
            <i class="bi bi-chevron-right"></i> Two-Bedroom Flat</a></li>
          <li><a href="${pageContext.request.contextPath}/properties?type=5">
            <i class="bi bi-chevron-right"></i> Duplex</a></li>
        </ul>
      </div>

      <div class="col-lg-4 col-md-6">
        <h6 class="footer-heading">Contact Information</h6>
        <ul class="footer-links">
          <li>
            <a href="#">
              <i class="bi bi-geo-alt-fill" style="color:var(--re-secondary)"></i>
              Adeseun Ogundoyin Polytechnic, Eruwa, Oyo State
            </a>
          </li>
          <li>
            <a href="mailto:admin@aoprealestate.edu.ng">
              <i class="bi bi-envelope-fill" style="color:var(--re-secondary)"></i>
              admin@aoprealestate.edu.ng
            </a>
          </li>
          <li>
            <a href="tel:+2348000000000">
              <i class="bi bi-telephone-fill" style="color:var(--re-secondary)"></i>
              +234 800 000 0000
            </a>
          </li>
        </ul>

        <div class="mt-3 p-3 rounded-3"
             style="background:rgba(255,255,255,0.05); border:1px solid rgba(255,255,255,0.1)">
          <p style="font-size:0.75rem;color:rgba(255,255,255,0.6);
                     font-weight:700;text-transform:uppercase;
                     letter-spacing:0.06em;margin-bottom:0.5rem;">
            Campus Distance Key
          </p>
          <div style="display:flex;flex-direction:column;gap:4px">
            <span style="font-size:0.75rem;color:rgba(255,255,255,0.7)">
              🏫 On Campus &nbsp;&nbsp;
              🚶 &lt;5 min &nbsp;&nbsp;
              🚶 5-10 min</span>
            <span style="font-size:0.75rem;color:rgba(255,255,255,0.7)">
              🚗 10-15 min &nbsp;&nbsp;
              🚗 &gt;15 min</span>
          </div>
        </div>
      </div>

    </div></div><div class="footer-bottom">
    <div class="container">
      <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
        <p style="font-size:0.82rem;color:rgba(255,255,255,0.5);margin:0">
          &copy; <%= currentYear %> AOPE Real Estate Listing System.
          ND2 Computer Science Final Year Project -
          Adeseun Ogundoyin Polytechnic, Eruwa.
        </p>
        <div style="display:flex;gap:1rem">
          <a href="#" style="font-size:0.78rem;color:rgba(255,255,255,0.4)">Privacy Policy</a>
          <a href="#" style="font-size:0.78rem;color:rgba(255,255,255,0.4)">Terms of Use</a>
        </div>
      </div>
    </div>
  </div>
</footer>