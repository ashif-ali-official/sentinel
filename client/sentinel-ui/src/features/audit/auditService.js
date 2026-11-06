import apiClient from "@/services/apiClient";

export const auditService = {
  async getCaseAudit(caseId) {
    const response = await apiClient.get(`/api/v1/cases/${caseId}/audit`);
    return response.data;
  },
};
