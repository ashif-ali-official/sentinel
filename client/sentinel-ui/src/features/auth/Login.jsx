import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signIn, getCurrentUser, } from "aws-amplify/auth";
import {
    ShieldCheck,
    Eye,
    EyeOff,
    LockKeyhole,
    Mail,
    ArrowRight,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/features/auth/AuthContext";

import {
  getAuthenticatedUser,
  getAuthTokens,
} from "@/features/auth/authService";

function getDashboardPath(role) {
    if (role === "PLAYER") return "/player/dashboard";
    if (role === "ADMIN" || role === "MODERATOR") return "/dashboard";
    return "/unauthorized";
}

function Login() {
    const { refreshAuth } = useAuth();
    const navigate = useNavigate();

    const [showPassword, setShowPassword] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        setMessage("");
        setLoading(true);

        try {
            // Check whether a user is already authenticated.
            try {
                await getCurrentUser();
                await refreshAuth();
                // Existing authenticated session.
                navigate(getDashboardPath(role), { replace: true });
                return;
            } catch (error) {
            // No authenticated user. Continue with sign-in.
            }

            const result = await signIn({
                username: email.trim(),
                password,
            });

            if (result.isSignedIn) {
                setPassword("");
                await refreshAuth();
                navigate(getDashboardPath(role), { replace: true });
                return;
            }

            setMessage(
            `Additional authentication required: ${result.nextStep.signInStep}`
            );
        } catch (error) {
            console.error("Authentication error:", error);

            setMessage(`${error.name}: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="grid min-h-screen lg:grid-cols-2">
        {/* Left branding section */}
        <div className="relative hidden flex-col justify-between overflow-hidden bg-slate-950 p-12 text-white lg:flex">
            <div className="absolute -right-32 -top-32 h-96 w-96 rounded-full bg-indigo-600/20 blur-3xl" />

            <div className="relative flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-indigo-600">
                <ShieldCheck size={26} />
            </div>

            <span className="text-2xl font-bold tracking-wider">
                SENTINEL
            </span>
            </div>

            <div className="relative max-w-lg space-y-6">
            <div className="inline-flex items-center gap-2 rounded-full border border-slate-700 px-4 py-2 text-sm text-slate-300">
                <span className="h-2 w-2 rounded-full bg-green-400" />
                Moderation Management Platform
            </div>

            <h1 className="text-5xl font-bold leading-tight tracking-tight">
                A safer gaming
                <span className="block text-indigo-400">
                community starts here.
                </span>
            </h1>

            <p className="text-lg leading-relaxed text-slate-400">
                Sentinel provides a centralized platform for
                managing reports, investigating incidents and
                maintaining a safe gaming environment.
            </p>
            </div>

            <div className="relative flex items-center justify-between text-sm text-slate-500">
            <span>© 2026 Sentinel</span>
            <span>Secure Access Portal</span>
            </div>
        </div>

        {/* Right login section */}
        <div className="flex items-center justify-center bg-slate-50 px-6 py-12">
            <div className="w-full max-w-md space-y-8">

            {/* Mobile branding */}
            <div className="flex items-center justify-center gap-3 lg:hidden">
                <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-600 text-white">
                <ShieldCheck size={24} />
                </div>

                <span className="text-2xl font-bold tracking-wider text-slate-900">
                SENTINEL
                </span>
            </div>

            {/* Heading */}
            <div className="space-y-2">
                <h2 className="text-3xl font-bold tracking-tight text-slate-900">
                Welcome back
                </h2>

                <p className="text-sm text-slate-500">
                Sign in to access your Sentinel account.
                </p>
            </div>

            {/* Login form */}
            <form onSubmit={handleSubmit} className="space-y-5">

                <div className="space-y-2">
                <label
                    htmlFor="email"
                    className="text-sm font-medium text-slate-700"
                >
                    Email address
                </label>

                <div className="relative">
                    <Mail
                    size={18}
                    className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                    />

                    <input
                    id="email"
                    type="email"
                    autoComplete="username"
                    required
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder="Enter your email"
                    className="h-12 w-full rounded-lg border border-slate-200 bg-white pl-10 pr-4 text-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                    />
                </div>
                </div>

                <div className="space-y-2">
                <div className="flex items-center justify-between">
                    <label
                    htmlFor="password"
                    className="text-sm font-medium text-slate-700"
                    >
                    Password
                    </label>

                    <button
                    type="button"
                    disabled={loading}
                    className="text-xs font-medium text-indigo-600 hover:text-indigo-800"
                    >
                    Forgot password?
                    </button>
                </div>

                <div className="relative">
                    <LockKeyhole
                    size={18}
                    className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
                    />

                    <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    autoComplete="current-password"
                    required
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    placeholder="Enter your password"
                    className="h-12 w-full rounded-lg border border-slate-200 bg-white pl-10 pr-12 text-sm outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                    />

                    <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-700"
                    aria-label={showPassword ? "Hide password" : "Show password"}
                    >
                    {showPassword ? (
                        <EyeOff size={18} />
                    ) : (
                        <Eye size={18} />
                    )}
                    </button>
                </div>
                </div>

                <div className="flex items-center gap-2">
                <input
                    id="remember"
                    type="checkbox"
                    className="h-4 w-4 rounded border-slate-300 accent-indigo-600"
                />

                <label
                    htmlFor="remember"
                    className="text-sm text-slate-600"
                >
                    Remember me
                </label>
                </div>

                {message && (
                <div
                    role="alert"
                    className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800"
                >
                    {message}
                </div>
                )}

                <Button
                type="submit"
                disabled={loading}
                className="h-12 w-full bg-indigo-600 text-base hover:bg-indigo-700"
                >
                {loading ? "Signing in..." : "Sign in"}
                {!loading && <ArrowRight className="ml-2 h-4 w-4" />}
                </Button>

            </form>

            <div className="border-t border-slate-200 pt-6 text-center">
                <p className="text-xs text-slate-400">
                Protected by Sentinel Security
                </p>
            </div>

            </div>
        </div>
        </div>
    );

}

export default Login;