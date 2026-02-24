import { Link, NavLink } from "react-router-dom";
import { useCategories } from "../../features/category/hooks/useCategory";

export default function Navbar() {
  const { data: categories = [], loading } = useCategories();

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">
          Frieren Shop
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto align-items-lg-center">

            <li className="nav-item dropdown">
              <button
                className="nav-link dropdown-toggle btn btn-link"
                data-bs-toggle="dropdown"
                type="button"
              >
                Categories
              </button>

              <ul className="dropdown-menu">
                {categories.map(c => (
                  <li key={c.id}>
                    <Link
                      to={`/products?categoryId=${c.id}`}
                      className="dropdown-item"
                    >
                      {c.name}
                    </Link>
                  </li>
                ))}
              </ul>
            </li>

            <li className="nav-item">
              <NavLink
                to="/products"
                className={({ isActive }) =>
                  `nav-link ${isActive ? "active fw-bold" : ""}`
                }
              >
                Products
              </NavLink>
            </li>

            <li className="nav-item">
              <Link className="nav-link" to="/cart">
                Cart
              </Link>
            </li>

            <li className="nav-item">
              <Link className="nav-link" to="/login">
                Login
              </Link>
            </li>

          </ul>
        </div>
      </div>
    </nav>
  );
}