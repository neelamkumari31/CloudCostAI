package com.finops.cloudcost;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Trusted friend exemption pass
public class ApprovalController {

    @Autowired
    private WastedServerRepository serverRepository;

    // 1. THIS IS THE NEW ENDPOINT: React will call this to fetch your PostgreSQL database table list rows
    @GetMapping("/servers")
    public List<WastedServer> getAllServers() {
        System.out.println("📊 [REST API] React dashboard requested server list. Querying database rows...");
        return serverRepository.findAll();
    }

    // 2. YOUR EXISTING ENDPOINT: Handles email clicks and web button approval actions
    @GetMapping("/approve")
    public String approveFix(@RequestParam("token") String token) {
        System.out.println("🔌 [REST API] Intercepted incoming web approval request for token: " + token);
        Optional<WastedServer> serverOpt = serverRepository.findByApprovalToken(token);

        if (serverOpt.isEmpty()) {
            return "<h1>❌ Invalid Link</h1><p>This approval token does not exist or is corrupted.</p>";
        }

        WastedServer server = serverOpt.get();

        if (server.getTokenExpiry().isBefore(LocalDateTime.now())) {
            server.setStatus("EXPIRED");
            serverRepository.save(server);
            return "<h1>⏳ Link Expired</h1><p>Security window closed. Please request a new optimization token.</p>";
        }

        if ("APPROVED".equals(server.getStatus())) {
            return "<h1>⚠️ Already Processed</h1><p>This script has already been approved and executed.</p>";
        }

        server.setStatus("APPROVED");
        serverRepository.save(server);

        System.out.println("\n⚡ [INFRASTRUCTURE DEPLOYED] Executing Authorized AI Fix Script on AWS Cloud:");
        System.out.println("👉 " + server.getAiScript() + "\n");

        return "<h1>✅ Optimization Approved Successfully!</h1>" +
               "<p><b>Server ID:</b> " + server.getServerId() + "</p>" +
               "<p><b>Action:</b> " + server.getAiSummary() + "</p>" +
               "<p>The script has been securely dispatched to the infrastructure automation pipeline.</p>";
    }
}
