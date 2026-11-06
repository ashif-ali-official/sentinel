import {
    fetchAuthSession,
    getCurrentUser,
    signOut,
} from "aws-amplify/auth";

export async function getAuthenticatedUser() {
    return await getCurrentUser();
}

export async function getAuthTokens() {
    const session = await fetchAuthSession();
    return {
        accessToken: session.tokens?.accessToken?.toString(),
        idToken: session.tokens?.idToken?.toString(),
    };
}

export async function logout() {
  await signOut();
}