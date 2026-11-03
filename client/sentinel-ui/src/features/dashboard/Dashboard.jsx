
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
  TrendingUp,
  TrendingDown,
  FolderKanban,
  Clock,
  Activity,
  CheckCircle2,
  ArrowUpRight,
  ShieldAlert,
} from "lucide-react";

import {
  dashboardStats,
  casesByStatus,
  casesBySeverity,
  recentCases,
  recentActivity,
} from "./dashboardData";

const statusColors = ["#F59E0B", "#6366F1", "#10B981"];

function SeverityBadge({ severity }) {
  const styles = {
    Critical: "bg-red-100 text-red-700",
    High: "bg-orange-100 text-orange-700",
    Medium: "bg-yellow-100 text-yellow-700",
    Low: "bg-slate-100 text-slate-700",
  };

  return (
    <Badge className={styles[severity]}>
      {severity}
    </Badge>
  );
}

function StatusBadge({ status }) {
  const styles = {
    Pending: "bg-yellow-100 text-yellow-700",
    "In Progress": "bg-indigo-100 text-indigo-700",
    Resolved: "bg-green-100 text-green-700",
  };

  return (
    <Badge className={styles[status]}>
      {status}
    </Badge>
  );
}

function Dashboard() {
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

        <div className="text-sm text-slate-500">
          Last updated: Nov 04, 2026
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {dashboardStats.map((stat, index) => {
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
                    {stat.value}
                  </h2>

                  <div className="mt-2 flex items-center gap-2 text-xs">
                    {stat.trend === "up" ? (
                      <TrendingUp className="h-4 w-4 text-green-600" />
                    ) : (
                      <TrendingDown className="h-4 w-4 text-green-600" />
                    )}

                    <span className="font-medium text-green-600">
                      {stat.change}
                    </span>

                    <span className="text-slate-400">
                      {stat.description}
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
                    data={casesByStatus}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={65}
                    outerRadius={100}
                    paddingAngle={4}
                  >
                    {casesByStatus.map((entry, index) => (
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
              {casesByStatus.map((item, index) => (
                <div key={item.name} className="flex items-center gap-2">
                  <span
                    className="h-3 w-3 rounded-full"
                    style={{ backgroundColor: statusColors[index] }}
                  />
                  <span className="text-xs text-slate-500">
                    {item.name}: {item.value}
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
              Cases by Severity
            </CardTitle>
          </CardHeader>

          <CardContent>
            <div className="h-[280px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={casesBySeverity}
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

          <button className="flex items-center gap-1 text-sm font-medium text-indigo-600 hover:text-indigo-800">
            View all
            <ArrowUpRight size={16} />
          </button>
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
                {recentCases.map((item) => (
                  <tr
                    key={item.id}
                    className="border-b last:border-0 hover:bg-slate-50"
                  >
                    <td className="py-4 font-medium text-indigo-600">
                      {item.id}
                    </td>

                    <td className="py-4 font-medium text-slate-800">
                      {item.player}
                    </td>

                    <td className="py-4 text-slate-600">
                      {item.category}
                    </td>

                    <td className="py-4">
                      <SeverityBadge severity={item.severity} />
                    </td>

                    <td className="py-4">
                      <StatusBadge status={item.status} />
                    </td>

                    <td className="py-4 whitespace-nowrap text-slate-500">
                      {item.date}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>

      {/* Recent Activity */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-base">
            <ShieldAlert size={18} className="text-indigo-600" />
            Recent Moderator Activity
          </CardTitle>
        </CardHeader>

        <CardContent>
          <div className="space-y-5">
            {recentActivity.map((activity, index) => (
              <div key={index} className="flex items-start gap-3">
                <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-indigo-100 text-sm font-semibold text-indigo-700">
                  {activity.moderator.charAt(0)}
                </div>

                <div className="min-w-0 flex-1">
                  <p className="text-sm font-medium text-slate-800">
                    {activity.moderator}
                  </p>

                  <p className="text-sm text-slate-500">
                    {activity.action}
                  </p>

                  <p className="mt-1 text-xs text-indigo-600">
                    {activity.reference}
                  </p>
                </div>

                <span className="whitespace-nowrap text-xs text-slate-400">
                  {activity.time}
                </span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

    </div>
  );
}

export default Dashboard;
