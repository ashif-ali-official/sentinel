import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";

import {
  fetchAuthSession,
  getCurrentUser,
  signOut,
} from "aws-amplify/auth";

const AuthContext = createContext(null);

function getUserRole(group = []) {
    if(group.includes("ADMIN")) return "ADMIN";
    if(group.includes("MODERATOR")) return "MODERATOR";
    if(group.includes("PLAYER")) return "PLAYER";

    return null;
}

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [groups, setGroups] = useState([]);
    const [role, setRole] = useState(null);
    const [loading, setLoading] = useState(true);

    const refreshAuth = useCallback( async () => {
        try {

            const currentUser = await getCurrentUser();
            const session = await fetchAuthSession();
            const token = session.tokens?.idToken;
            if(!token){
                throw new Error("ID token is unavailable");
            }

            const userGroups = token.payload["cognito:groups"] || [];
            setUser(currentUser);
            setGroups(userGroups);
            setRole(getUserRole(userGroups));

        } catch {
            setUser(null);
            setGroups([]);
            setRole(null);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        refreshAuth();
    }, [refreshAuth]);

    const logout = async () => {
        console.log("Logout started");
        
        try{
            await signOut();
            console.log("Cognito signOut started");
            setUser(null);
            setGroups([]);
            setRole(null);
            console.log("Auth state cleared");
        } catch (error) {
            console.error("Cognito signout fialed:", error);
            throw error;
        }
    };

    const value = {
        user,
        groups,
        role,
        loading,
        isAuthenticated: !!user,
        refreshAuth,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
        {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}