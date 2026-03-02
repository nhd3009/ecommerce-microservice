import { Outlet } from "react-router-dom";

export default function AdminLayout() {
  return (
    <div className="d-flex">
      <aside style={{ width: 240 }} className="bg-dark text-white p-3">
        Admin Menu
      </aside>

      <main className="flex-fill p-4">
        <Outlet />
      </main>
    </div>
  );
}