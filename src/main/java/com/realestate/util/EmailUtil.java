package com.realestate.util;

/**
 * EmailUtil - Email Notification Utility
 *
 * Provides methods for sending system-generated email notifications.
 * In the current implementation, email content is logged to the
 * console for development purposes.
 *
 * To enable real email delivery, configure an SMTP server in the
 * sendEmail() method using JavaMail API and add mail.jar to WEB-INF/lib.
 *
 * @author  AOPE CS Department
 * @version 1.0
 */
public class EmailUtil {

    private static final String FROM_ADDRESS = "noreply@aoprealestate.edu.ng";
    private static final String SYSTEM_NAME  = "AOPE Real Estate";

    /**
     * Private constructor - utility class, not instantiable.
     */
    private EmailUtil() {}

    /**
     * Core email sending method.
     * Replace the console output with JavaMail SMTP code when ready.
     *
     * @param toEmail   Recipient email address
     * @param subject   Email subject line
     * @param htmlBody  HTML email body content
     */
    public static void sendEmail(String toEmail, String subject, String htmlBody) {
        // ----- Development Mode: Log to console -----
        System.out.println("========================================");
        System.out.println("[EmailUtil] SIMULATED EMAIL SEND");
        System.out.println("From    : " + FROM_ADDRESS);
        System.out.println("To      : " + toEmail);
        System.out.println("Subject : " + subject);
        System.out.println("Body    : " + htmlBody);
        System.out.println("========================================");

        // ----- Production Mode (JavaMail): Uncomment when SMTP is configured -----
        /*
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.yourmailserver.com");
        props.put("mail.smtp.port",            "587");

        Session mailSession = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your@email.com", "yourpassword");
            }
        });

        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(FROM_ADDRESS, SYSTEM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            System.err.println("[EmailUtil] Failed to send email: " + e.getMessage());
        }
        */
    }

    /**
     * Sends a password reset email with a reset link.
     *
     * @param toEmail     Recipient email address
     * @param resetToken  The generated reset token
     * @param baseUrl     The application base URL
     */
    public static void sendPasswordResetEmail(String toEmail,
                                               String resetToken,
                                               String baseUrl) {
        String resetLink = baseUrl + "/forgot-password?action=reset&token=" + resetToken;
        String subject   = SYSTEM_NAME + " - Password Reset Request";
        String body      =
            "<h2>Password Reset Request</h2>" +
            "<p>You requested a password reset for your AOP Real Estate account.</p>" +
            "<p>Click the link below to reset your password. " +
            "This link expires in <strong>60 minutes</strong>.</p>" +
            "<a href='" + resetLink + "' style='background:#1a3c5e;color:white;" +
            "padding:12px 24px;text-decoration:none;border-radius:5px;'>Reset Password</a>" +
            "<p>If you did not request this, please ignore this email.</p>" +
            "<p>- " + SYSTEM_NAME + " Team</p>";
        sendEmail(toEmail, subject, body);
    }

    /**
     * Sends a property approval notification to the landlord.
     *
     * @param toEmail       Landlord's email address
     * @param landlordName  Landlord's full name
     * @param propertyTitle Title of the approved property
     * @param propertyId    The property's database ID
     * @param baseUrl       The application base URL
     */
    public static void sendPropertyApprovedEmail(String toEmail, String landlordName,
                                                  String propertyTitle, int propertyId,
                                                  String baseUrl) {
        String propertyLink = baseUrl + "/property?id=" + propertyId;
        String subject = SYSTEM_NAME + " - Your Listing Has Been Approved";
        String body    =
            "<h2>Congratulations, " + landlordName + "!</h2>" +
            "<p>Your property listing <strong>\"" + propertyTitle +
            "\"</strong> has been reviewed and approved by our team.</p>" +
            "<p>Your listing is now live and visible to all property seekers.</p>" +
            "<a href='" + propertyLink + "' style='background:#198754;color:white;" +
            "padding:12px 24px;text-decoration:none;border-radius:5px;'>View Your Listing</a>" +
            "<p>- " + SYSTEM_NAME + " Team</p>";
        sendEmail(toEmail, subject, body);
    }

    /**
     * Sends a new inquiry notification to the property owner.
     *
     * @param toEmail       Owner's email address
     * @param ownerName     Owner's full name
     * @param propertyTitle Title of the property inquired about
     * @param senderName    Name of the person who sent the inquiry
     * @param baseUrl       The application base URL
     */
    public static void sendInquiryNotificationEmail(String toEmail, String ownerName,
                                                     String propertyTitle, String senderName,
                                                     String baseUrl) {
        String inquiryLink = baseUrl + "/landlord/manage-properties";
        String subject = SYSTEM_NAME + " - New Inquiry for \"" + propertyTitle + "\"";
        String body    =
            "<h2>New Property Inquiry</h2>" +
            "<p>Dear " + ownerName + ",</p>" +
            "<p><strong>" + senderName + "</strong> has sent an inquiry about your property " +
            "<strong>\"" + propertyTitle + "\"</strong>.</p>" +
            "<p>Log in to your dashboard to view and reply to the inquiry.</p>" +
            "<a href='" + inquiryLink + "' style='background:#1a3c5e;color:white;" +
            "padding:12px 24px;text-decoration:none;border-radius:5px;'>View Inquiry</a>" +
            "<p>- " + SYSTEM_NAME + " Team</p>";
        sendEmail(toEmail, subject, body);
    }
}