package com.finops.cloudcost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wasted_servers")
public class WastedServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serverId;
    private int cpuUtilization;
    private int monthlyCostINR;
    private String status; // PENDING_APPROVAL, AUTOMATED_FIX, EXCLUDED

    @Column(columnDefinition = "TEXT")
    private String aiSummary;

    @Column(columnDefinition = "TEXT")
    private String aiScript;

    private String approvalToken;
    private java.time.LocalDateTime tokenExpiry;


    // Standard constructor required by JPA
    public WastedServer() {}

    public WastedServer(String serverId, int cpuUtilization, int monthlyCostINR, String status) {
        this.serverId = serverId;
        this.cpuUtilization = cpuUtilization;
        this.monthlyCostINR = monthlyCostINR;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getServerId() { return serverId; }
    public void setServerId(String serverId) { this.serverId = serverId; }
    public int getCpuUtilization() { return cpuUtilization; }
    public void setCpuUtilization(int cpuUtilization) { this.cpuUtilization = cpuUtilization; }
    public int getMonthlyCostINR() { return monthlyCostINR; }
    public void setMonthlyCostINR(int monthlyCostINR) { this.monthlyCostINR = monthlyCostINR; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public String getAiScript() { return aiScript; }
    public void setAiScript(String aiScript) { this.aiScript = aiScript; }
    public String getApprovalToken() { return approvalToken; }
    public void setApprovalToken(String approvalToken) { this.approvalToken = approvalToken; }
    public java.time.LocalDateTime getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(java.time.LocalDateTime tokenExpiry) { this.tokenExpiry = tokenExpiry; }

}
