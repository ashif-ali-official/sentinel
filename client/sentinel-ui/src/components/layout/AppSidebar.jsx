import { ClipboardList, FolderKanban, Users, LayoutDashboard, Settings, ShieldCheck, ChevronLeft } from 'lucide-react';
import { NavLink } from 'react-router-dom'

const navigation = [
    {
        title: "OVERVIEW",
        items: [
            {
                label: "Dashboard",
                path: "/dashboard",
                icon: LayoutDashboard
            },
        ],
    },
    {
        title: "MODERATION",
        items: [
            {
                label: "Cases",
                path: "/cases",
                icon: FolderKanban
            },
            {
                label: "Players",
                path: "/players",
                icon: Users
            },
            {
                label: "Audit History",
                path: "/audit",
                icon: ClipboardList
            },
        ],
    },
    {
        title: "SYSTEM",
        items: [
            {
                label: "Settings",
                path: "/settings",
                icon: Settings
            },
        ],
    },
];

function AppSidebar() {
  return (
    <aside className="flex h-screen w-64 shrink-0 flex-col bg-slate-950 text-slate-300">
      {/* Branding */}
      <div className="flex h-16 items-center gap-3 border-b border-slate-800 px-6">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-indigo-600 text-white">
          <ShieldCheck size={22} />
        </div>
        <div>
          <h1 className="text-lg font-bold tracking-wide text-white">
            SENTINEL
          </h1>
          <p className="text-xs text-slate-500">
            Moderation Platform
          </p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 space-y-7 overflow-y-auto px-4 py-6">
        {navigation.map((group) => (
          <div key={group.title}>
            <p className="mb-3 px-3 text-xs font-semibold tracking-wider text-slate-500">
              {group.title}
            </p>

            <div className="space-y-1">
              {group.items.map((item) => {
                const Icon = item.icon;

                return (
                  <NavLink
                    key={item.path}
                    to={item.path}
                    className={({ isActive }) =>
                      `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors ${
                        isActive
                          ? "bg-indigo-600 text-white"
                          : "text-slate-400 hover:bg-slate-800 hover:text-white"
                      }`
                    }
                  >
                    <Icon size={18} />
                    <span>{item.label}</span>
                  </NavLink>
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      {/* Footer */}
      <div className="border-t border-slate-800 p-4">
        <div className="flex items-center gap-3 rounded-lg bg-slate-900 p-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-indigo-500 text-sm font-semibold text-white">
            AD
          </div>
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-medium text-white">
              Administrator
            </p>
            <p className="text-xs text-slate-500">
              System Admin
            </p>
          </div>
          <ChevronLeft size={16} className="rotate-180 text-slate-500" />
        </div>
      </div>
    </aside>
  )
}

export default AppSidebar;