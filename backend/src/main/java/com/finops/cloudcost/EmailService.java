package com.finops.cloudcost;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendApprovalEmail(String targetManagerEmail, String serverId, String summary, String approvalUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(targetManagerEmail);
            helper.setSubject("⚠️ [CRITICAL] Cloud FinOps Cost Optimization Approval Request: " + serverId);

            // Constructing a beautiful, interactive corporate HTML email layout template
            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; max-width: 600px; border-radius: 8px;'>"
                    + "<h2 style='color: #d32f2f; margin-top: 0;'>⚠️ Infrastructure Alert: Underutilized Server Detected</h2>"
                    + "<p>Our FinOps AI monitor has flagged an inefficient production resource on your cloud environment.</p>"
                    + "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>"
                    + "<table style='width: 100%; border-collapse: collapse;'>"
                    + "<tr><td style='padding: 6px 0; <b>Server ID:</b></td><td><code>" + serverId + "</code></td></tr>"
                    + "<tr><td style='padding: 6px 0; <b>AI Diagnosis:</b></td><td>" + summary + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top: 25px;'>If you approve this recommendation, click the secure single-tap validation button below to execute the automated downgrade script:</p>"
                    + "<div style='margin-top: 25px; text-align: center;'>"
                    + "  <a href='" + approvalUrl + "' style='background-color: #1a73e8; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold; display: inline-block;'>Approve & Execute Optimization Fix</a>"
                    + "</div>"
                    + "<p style='color: #666; font-size: 12px; margin-top: 30px; text-align: center;'>Security Note: This single-use confirmation authorization token expires automatically in 15 minutes.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            System.out.println("📬 [NOTIFIER] Dispatching secure HTML approval email wrapper to: " + targetManagerEmail);
            mailSender.send(message);
            System.out.println("✅ [NOTIFIER] Email delivered successfully through security gateway!");

        } catch (Exception e) {
            System.err.println("❌ Failed to dispatch email notification: " + e.getMessage());
        }
    }
}
