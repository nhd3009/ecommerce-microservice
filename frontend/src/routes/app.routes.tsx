import AdminLayout from "../components/layout/AdminLayout";
import UserLayout from "../components/layout/ClientLayout";
import HomePage from "../features/client/components/HomePage";
import ProductDetailPage from "../features/product/components/ProductDetailPage";
import ProductList from "../features/product/components/ProductList";


export const appRoutes = [
  {
    element: <UserLayout />,
    children: [
      { path: "/", element: <HomePage /> },
      { path: "/products/:id", element: <ProductDetailPage /> },
      { path: "/products", element: <ProductList /> },
    ],
  },
  {
    path: "/admin",
    element: <AdminLayout />,
    children: [
      { path: "products", element: <div>Admin Products</div> },
    ],
  },
];