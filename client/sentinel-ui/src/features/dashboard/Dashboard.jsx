
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { Badge } from "@/components/ui/badge";

import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts";

import {
  FolderKanban,
  Clock,
  Activity,
  CheckCircle2,
  ArrowUpRight,
  ShieldAlert,
} from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { caseService } from "@/features/cases/caseService";
import { auditService } from "@/features/audit/auditService";
import { Button } from "@/components/ui/button";

const statusColors = ["#3B82F6", "#8B5CF6", "#6366F1", "#10B981", "#64748B"];
const statusNames = ["OPEN", "ASSIGNED", "UNDER_INVESTIGATION", "RESOLVED", "CLOSED"];
const priorityNames = ["CRITICAL", "HIGH", "MEDIUM", "LOW"];

function formatDate(date) {
  if (!date) return "-";
  return new Date(date).toLocaleDateString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
  });
}

function summarizeCases(cases) {
  const count = (predicate) => cases.filter(predicate).length;
  return {
    stats: [
      { title: "Total Cases", value: cases.length },
      { title: "Open Cases", value: count((item) => item.status === "OPEN") },
      { title: "In Progress", value: count((item) => ["ASSIGNED", "UNDER_INVESTIGATION"].includes(item.status)) },
      { title: "Resolved Cases", value: count((item) => ["RESOLVED", "CLOSED"].includes(item.status)) },
    ],
    byStatus: statusNames.map((name) => ({ name, value: count((item) => item.status === name) })),
    byPriority: priorityNames.map((name) => ({ name, cases: count((item) => item.priority === name) })),
    recent: [...cases].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 5),
  };
}

async function loadDashboardData() {
  const cases = await caseService.getAllCases();
  const latestCases = [...cases]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 10);
  const auditResults = await Promise.allSettled(
    latestCases.map((item) => auditService.getCaseAudit(item.caseId)),
  );
  const activities = auditResults
    .flatMap((result) => result.status === "fulfilled" ? result.value : [])
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5);
  return { cases, activities };
}

function PriorityBadge({ priority }) {
  const styles = {
    CRITICAL: "bg-red-100 text-red-700",
    HIGH: "bg-orange-100 text-orange-700",
    MEDIUM: "bg-yellow-100 text-yellow-700",
    LOW: "bg-slate-100 text-slate-700",
  };

  return (
      <Badge className={styles[priority]}>
      {priority?.replaceAll("_", " ")}
    </Badge>
  );
}

function StatusBadge({ status }) {
  const styles = {
    OPEN: "bg-blue-100 text-blue-700",
    ASSIGNED: "bg-purple-100 text-purple-700",
    UNDER_INVESTIGATION: "bg-indigo-100 text-indigo-700",
    RESOLVED: "bg-green-100 text-green-700",
    CLOSED: "bg-slate-100 text-slate-700",
  };

  return (
    <Badge className={styles[status]}>
      {status?.replaceAll("_", " ")}
    </Badge>
  );
}

function Dashboard() {
  const { data, isLoading, isError, error, refetch } = useQuery({
    queryKey: ["dashboard-cases"],
    queryFn: loadDashboardData,
  });
  const cases = data?.cases || [];
  const activities = data?.activities || [];
  const { stats, byStatus, byPriority, recent } = summarizeCases(cases);
  const icons = [
    FolderKanban,
    Clock,
    Activity,
    CheckCircle2,
  ];

  return (
    <div className="space-y-6">

      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-slate-900">
            Dashboard
          </h1>
          <p className="mt-1 text-sm text-slate-500">
            Monitor moderation activities and platform performance.
          </p>
        </div>

        <Button variant="outline" onClick={() => refetch()} disabled={isLoading}>Refresh</Button>
      </div>

      {/* KPI Cards */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {stats.map((stat, index) => {
          const Icon = icons[index];

          return (
            <Card key={stat.title}>
              <CardContent className="p-5">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-medium text-slate-500">
                    {stat.title}
                  </p>

                  <div className="rounded-lg bg-indigo-50 p-2">
                    <Icon className="h-5 w-5 text-indigo-600" />
                  </div>
                </div>

                <div className="mt-4">
                  <h2 className="text-3xl font-bold text-slate-900">
                    {isLoading ? "…" : stat.value}
                  </h2>

                  <div className="mt-2 flex items-center gap-2 text-xs">
                    <span className="text-slate-400">
                      Live count from case service
                    </span>
                  </div>
                </div>
              </CardContent>
            </Card>
          );
        })}
      </div>

      {/* Analytics */}
      <div className="grid gap-6 lg:grid-cols-2">

        {/* Cases by Status */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              Cases by Status
            </CardTitle>
          </CardHeader>

          <CardContent>
            <div className="h-[280px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={byStatus}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={65}
                    outerRadius={100}
                    paddingAngle={4}
                  >
                    {byStatus.map((entry, index) => (
                      <Cell
                        key={entry.name}
                        fill={statusColors[index]}
                      />
                    ))}
                  </Pie>

                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>

            <div className="flex flex-wrap justify-center gap-5">
              {byStatus.map((item, index) => (
                <div key={item.name} className="flex items-center gap-2">
                  <span
                    className="h-3 w-3 rounded-full"
                    style={{ backgroundColor: statusColors[index] }}
                  />
                  <span className="text-xs text-slate-500">
                    {item.name.replaceAll("_", " ")}: {item.value}
                  </span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Cases by Severity */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">
              Cases by Priority
            </CardTitle>
          </CardHeader>

          <CardContent>
            <div className="h-[280px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={byPriority}
                  margin={{ top: 10, right: 10, left: -15, bottom: 5 }}
                >
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />

                  <XAxis
                    dataKey="name"
                    tickLine={false}
                    axisLine={false}
                    fontSize={12}
                  />

                  <YAxis
                    tickLine={false}
                    axisLine={false}
                    fontSize={12}
                  />

                  <Tooltip />

                  <Bar
                    dataKey="cases"
                    fill="#6366F1"
                    radius={[6, 6, 0, 0]}
                    barSize={40}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Cases */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="text-base">
            Recent Cases
          </CardTitle>

          <Link to="/cases" className="flex items-center gap-1 text-sm font-medium text-indigo-600 hover:text-indigo-800">
            View all
            <ArrowUpRight size={16} />
          </Link>
        </CardHeader>

        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b text-left text-xs text-slate-500">
                  <th className="pb-3 font-medium">Case ID</th>
                  <th className="pb-3 font-medium">Player</th>
                  <th className="pb-3 font-medium">Category</th>
                  <th className="pb-3 font-medium">Severity</th>
                  <th className="pb-3 font-medium">Status</th>
                  <th className="pb-3 font-medium">Date</th>
                </tr>
              </thead>

              <tbody>
                {isLoading ? <tr><td colSpan="6" className="py-8 text-center text-slate-500">Loading cases…</td></tr> : isError ? <tr><td colSpan="6" className="py-8 text-center text-red-600">Could not load cases.</td></tr> : recent.length === 0 ? <tr><td colSpan="6" className="py-8 text-center text-slate-500">No cases found.</td></tr> : recent.map((item) => (
                  <tr
                    key={item.caseId}
                    className="border-b last:border-0 hover:bg-slate-50"
                  >
                    <td className="py-4 font-medium text-indigo-600">
                      {item.caseId}
                    </td>

                    <td className="py-4 font-medium text-slate-800">
                      {item.reportedPlayerId}
                    </td>

                    <td className="py-4 text-slate-600">
                      {item.category?.replaceAll("_", " ")}
                    </td>

                    <td className="py-4">
                      <PriorityBadge priority={item.priority} />
                    </td>

                    <td className="py-4">
                      <StatusBadge status={item.status} />
                    </td>

                    <td className="py-4 whitespace-nowrap text-slate-500">
                      {formatDate(item.createdAt)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      {isError && <p role="alert" className="text-sm text-red-600">Could not load dashboard data: {error?.message}</p>}

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <ShieldAlert size={18} className="text-indigo-600" />
            Recent Case Activity
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <p className="text-sm text-slate-500">Loading recent activity…</p>
          ) : activities.length === 0 ? (
            <p className="text-sm text-slate-500">No recent audit activity found.</p>
          ) : (
            <div className="space-y-4">
              {activities.map((activity) => (
                <div key={activity.auditId} className="flex items-start justify-between gap-4 border-b pb-4 last:border-0 last:pb-0">
                  <div>
                    <p className="text-sm font-medium text-slate-800">{activity.action?.replaceAll("_", " ") || "Case updated"}</p>
                    <p className="text-sm text-slate-500">{activity.caseId} · {activity.performedBy || "Unknown user"}</p>
                  </div>
                  <time className="whitespace-nowrap text-xs text-slate-400">{formatDate(activity.createdAt)}</time>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

    </div>
  );
}

export default Dashboard;
