import axios from "axios";
import { fetchAuthSession } from "aws-amplify/auth";

export function createApiClient(baseURL) {
  const client = axios.create({
    baseURL,
    headers: {
      "Content-Type": "application/json",
    },
    timeout: 10000,
  });

  client.interceptors.request.use(async (config) => {
    const session = await fetchAuthSession();
    const token = session.tokens?.accessToken?.toString();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  });

  return client;
}

const apiClient = createApiClient(
  import.meta.env.VITE_CASE_SERVICE_URL || "http://localhost:8082",
);

export default apiClient;
