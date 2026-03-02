import { useRoutes } from "react-router-dom";
import { appRoutes } from "./routes/app.routes";

export default function App() {
  const routes = useRoutes(appRoutes);
  return routes;
}