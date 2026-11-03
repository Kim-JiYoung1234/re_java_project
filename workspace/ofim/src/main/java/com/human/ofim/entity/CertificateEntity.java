/*
 * package com.human.ofim.entity;
 * 
 * import com.human.ofim.dto.CertificateDto;
 * 
 * import jakarta.persistence.Column; import jakarta.persistence.Entity; import
 * jakarta.persistence.Id; import jakarta.persistence.Table; import lombok.Data;
 * 
 * @Data
 * 
 * @Entity
 * 
 * @Table(name = "certification") public class CertificateEntity {
 * 
 * public CertificateEntity() { // 기본 생성자 }
 * 
 * @Id
 * 
 * @Column(name = "member_id") private Integer memberId;
 * 
 * @Column(name = "cert_name", nullable = false) private String certName;
 * private String issuer;
 * 
 * @Column(name = "cert_no", unique = true, nullable = false) private String
 * certNo;
 * 
 * @Column(name = "issue_date") private String issueDate;
 * 
 * @Column(name = "expired_date") private String expiredDate;
 * 
 * @Column(name = "cert_file_path") private String certFilePath;
 * 
 * @Column(name = "reject_reason") private String rejectReason;
 * 
 * public CertificateEntity(Integer memberId, CertificateDto certificateDto) {
 * 
 * this.memberId = memberId; this.certName = certificateDto.getCertName();
 * this.issuer = certificateDto.getIssuer(); this.certNo =
 * certificateDto.getCertNo(); this.issueDate = certificateDto.getIssueDate();
 * this.expiredDate = certificateDto.getExpiredDate(); this.certFilePath =
 * certificateDto.getCertFilePath(); this.rejectReason =
 * certificateDto.getRejectReason(); }
 * 
 * 
 * 
 * }
 */