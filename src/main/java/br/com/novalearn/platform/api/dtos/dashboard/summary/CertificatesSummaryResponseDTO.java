package br.com.novalearn.platform.api.dtos.dashboard.summary;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificatesSummaryResponseDTO {
    private final long certificatesIssued;

    public CertificatesSummaryResponseDTO(long certificatesIssued) {
        this.certificatesIssued = certificatesIssued;
    }

    public long getCertificatesIssued() { return certificatesIssued; }
}