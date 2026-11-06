import { createApiClient } from "@/services/apiClient";

const apiClient = createApiClient(
  import.meta.env.VITE_PLAYER_SERVICE_URL || "http://localhost:8084",
);

export const playerService = {
  async getPlayers(params = {}) {
    const response = await apiClient.get("/api/v1/players", { params });
    return response.data;
  },
};
