import { createApiClient } from "@/services/apiClient";

const apiClient = createApiClient(
  import.meta.env.VITE_PLAYER_SERVICE_URL || "http://localhost:8084",
  { includeIdentityToken: true },
);

export const playerService = {
  async getCurrentPlayer() {
    const response = await apiClient.get("/api/v1/players/me");
    return response.data;
  },

  async getPlayers(params = {}) {
    const response = await apiClient.get("/api/v1/players", { params });
    return response.data;
  },
};
