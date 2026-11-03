import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Search, RefreshCw, FolderKanban } from "lucide-react";

import { caseService } from "./caseService";

import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";

const PAGE_SIZE = 10;

const statuses = [
  "OPEN",
  "ASSIGNED",
  "UNDER_INVESTIGATION",
  "RESOLVED",
  "CLOSED",
];

const priorities = [
  "LOW",
  "MEDIUM",
  "HIGH",
  "CRITICAL",
];

function getStatusStyle(status) {
  const styles = {
    OPEN: "bg-blue-100 text-blue-700",
    ASSIGNED: "bg-purple-100 text-purple-700",
    UNDER_INVESTIGATION: "bg-indigo-100 text-indigo-700",
    RESOLVED: "bg-green-100 text-green-700",
    CLOSED: "bg-slate-100 text-slate-700",
  };

  return styles[status] || "bg-slate-100 text-slate-700";
}

function getPriorityStyle(priority) {
  const styles = {
    LOW: "bg-slate-100 text-slate-700",
    MEDIUM: "bg-yellow-100 text-yellow-700",
    HIGH: "bg-orange-100 text-orange-700",
    CRITICAL: "bg-red-100 text-red-700",
  };

  return styles[priority] || "bg-slate-100 text-slate-700";
}

function formatDate(date) {
  if (!date) return "-";

  return new Date(date).toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

function Cases() {

  const [filters, setFilters] = useState({
    status: "",
    priority: "",
    reportedPlayerId: "",
  });

  const [appliedFilters, setAppliedFilters] = useState({});
  const [page, setPage] = useState(0);

  const {
    data,
    isLoading, 
    isError, 
    error, 
    refetch, 
    isFetching, 
  } = useQuery({
    queryKey: ["cases", appliedFilters, page],
    queryFn: () => 
      caseService.getCases({
        ...appliedFilters,
        page,
        size: PAGE_SIZE,
      }),
  });

  const cases = data?.content || [];
  const totalPages = data?.totalPages || 0;
  const totalElements = data?.totalElements || 0;

  function handleFilterChange(event) {
    const { name, value } = event.target;

    setFilters((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  function handleSearch(event) {
    event.preventDefault();

    const nextFilters = Object.fromEntries(
      Object.entries(filters).filter(([, value]) => value.trim() !== "")
    );

    setAppliedFilters(nextFilters);
    setPage(0);
  }

  function handleReset() {
    setFilters({
      status: "",
      priority: "",
      reportedPlayerId: "",
    });

    setAppliedFilters({});
    setPage(0);
  }

  return (
    <div className="space-y-6">

      {/* Page heading */}
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">
            Case Management
          </h1>

          <p className="mt-1 text-sm text-slate-500">
            Review, investigate and manage moderation cases.
          </p>
        </div>

        <Button
          variant="outline"
          onClick={() => refetch()}
          disabled={isFetching}
        >
          <RefreshCw
            className={`mr-2 h-4 w-4 ${isFetching ? "animate-spin" : ""}`}
          />
          Refresh
        </Button>
      </div>

      {/* Summary */}
      <Card>
        <CardContent className="flex items-center gap-4 p-5">
          <div className="rounded-xl bg-indigo-100 p-3">
            <FolderKanban className="h-6 w-6 text-indigo-600" />
          </div>

          <div>
            <p className="text-sm text-slate-500">
              Total matching cases
            </p>

            <p className="text-2xl font-bold text-slate-900">
              {isLoading ? "..." : totalElements}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* Filters */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">
            Filters
          </CardTitle>
        </CardHeader>

        <CardContent>
          <form
            onSubmit={handleSearch}
            className="grid gap-4 md:grid-cols-2 xl:grid-cols-4"
          >
            <div className="space-y-2">
              <label className="text-sm font-medium">
                Reported Player ID
              </label>

              <Input
                name="reportedPlayerId"
                value={filters.reportedPlayerId}
                onChange={handleFilterChange}
                placeholder="Enter player ID"
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">
                Status
              </label>

              <select
                name="status"
                value={filters.status}
                onChange={handleFilterChange}
                className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
              >
                <option value="">All statuses</option>

                {statuses.map((status) => (
                  <option key={status} value={status}>
                    {status.replaceAll("_", " ")}
                  </option>
                ))}
              </select>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">
                Priority
              </label>

              <select
                name="priority"
                value={filters.priority}
                onChange={handleFilterChange}
                className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
              >
                <option value="">All priorities</option>

                {priorities.map((priority) => (
                  <option key={priority} value={priority}>
                    {priority}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-end gap-2">
              <Button type="submit" className="flex-1">
                <Search className="mr-2 h-4 w-4" />
                Search
              </Button>

              <Button
                type="button"
                variant="outline"
                onClick={handleReset}
              >
                Reset
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* Cases table */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">
            Cases
          </CardTitle>
        </CardHeader>

        <CardContent>
          {isLoading ? (
            <div className="py-12 text-center text-sm text-slate-500">
              Loading cases...
            </div>
          ) : isError ? (
            <div className="space-y-3 py-10 text-center">
              <p className="font-medium text-red-600">
                Failed to load cases.
              </p>

              <p className="text-sm text-slate-500">
                {error?.message || "Please check the Case Service connection."}
              </p>

              <Button variant="outline" onClick={() => refetch()}>
                Retry
              </Button>
            </div>
          ) : cases.length === 0 ? (
            <div className="py-12 text-center text-sm text-slate-500">
              No cases found.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b text-left text-xs text-slate-500">
                    <th className="whitespace-nowrap pb-3 pr-4 font-medium">
                      Case ID
                    </th>

                    <th className="whitespace-nowrap pb-3 pr-4 font-medium">
                      Reported Player
                    </th>

                    <th className="whitespace-nowrap pb-3 pr-4 font-medium">
                      Category
                    </th>

                    <th className="whitespace-nowrap pb-3 pr-4 font-medium">
                      Priority
                    </th>

                    <th className="whitespace-nowrap pb-3 pr-4 font-medium">
                      Status
                    </th>

                    <th className="whitespace-nowrap pb-3 font-medium">
                      Created At
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {cases.map((item) => (
                    <tr
                      key={item.caseId}
                      className="border-b last:border-0 hover:bg-slate-50"
                    >
                      <td className="whitespace-nowrap py-4 pr-4 font-medium text-indigo-600">
                        {item.caseId}
                      </td>

                      <td className="whitespace-nowrap py-4 pr-4 text-slate-700">
                        {item.reportedPlayerId}
                      </td>

                      <td className="whitespace-nowrap py-4 pr-4 text-slate-600">
                        {item.category?.replaceAll("_", " ")}
                      </td>

                      <td className="whitespace-nowrap py-4 pr-4">
                        <Badge className={getPriorityStyle(item.priority)}>
                          {item.priority}
                        </Badge>
                      </td>

                      <td className="whitespace-nowrap py-4 pr-4">
                        <Badge className={getStatusStyle(item.status)}>
                          {item.status?.replaceAll("_", " ")}
                        </Badge>
                      </td>

                      <td className="whitespace-nowrap py-4 text-slate-500">
                        {formatDate(item.createdAt)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Pagination */}
          {!isLoading && !isError && totalElements > 0 && (
            <div className="mt-5 flex flex-col justify-between gap-3 border-t pt-4 sm:flex-row sm:items-center">
              <p className="text-sm text-slate-500">
                Page {page + 1} of {totalPages}
                {" · "}
                {totalElements} cases
              </p>

              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={page === 0 || isFetching}
                  onClick={() => setPage((previous) => previous - 1)}
                >
                  Previous
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  disabled={page + 1 >= totalPages || isFetching}
                  onClick={() => setPage((previous) => previous + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

export default Cases;