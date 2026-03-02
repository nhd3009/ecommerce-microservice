import { Outlet } from "react-router-dom";
import Navbar from "./navbar";

export default function UserLayout() {
  return (
    <>
      <Navbar />
      <main className="container my-4">
        <Outlet />
      </main>
    </>
  );
}