import { createApiClient } from "@/services/apiClient";

const apiClient = createApiClient(
  import.meta.env.VITE_REPORT_SERVICE_URL,
  { includeIdentityToken: true },
);

export const playerReportService = {
  async getMyReports(params = {}) {
    const response = await apiClient.get("/api/v1/reports/me", { params });
    return response.data;
  },
};
