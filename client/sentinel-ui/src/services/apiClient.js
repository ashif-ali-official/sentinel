import axios from "axios";
import { fetchAuthSession } from "aws-amplify/auth";

function requiredServiceUrl(url, variableName) {
  if (!url) {
    throw new Error(`${variableName} must be set to the service URL.`);
  }
  return url;
}

export function createApiClient(baseURL, { includeIdentityToken = false } = {}) {
  if (!baseURL) {
    throw new Error("A VITE_*_SERVICE_URL value is required for each API client.");
  }

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
    const identityToken = includeIdentityToken
      ? session.tokens?.idToken?.toString()
      : null;
    if (identityToken) config.headers["X-Identity-Token"] = identityToken;
    return config;
  });

  return client;
}

const apiClient = createApiClient(
  requiredServiceUrl(import.meta.env.VITE_CASE_SERVICE_URL, "VITE_CASE_SERVICE_URL"),
);

export default apiClient;
