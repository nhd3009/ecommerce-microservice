import { Link } from "react-router-dom";
import { useHomepageProducts } from "../hooks/useHomePage";
import { API_BASE_URL } from "../../../utils/constants";

export default function HomePage() {
  const { data, loading, error } = useHomepageProducts();

  if (loading) {
    return (
      <div className="text-center mt-5">
        <div className="spinner-border text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger text-center mt-5">
        {error}
      </div>
    );
  }

  return (
    <div className="container">

      <div className="bg-light p-5 rounded mb-5 text-center">
        <h1 className="display-5 fw-bold">Welcome to Frieren Shop ✨</h1>
        <p className="lead">Discover the newest products today</p>
      </div>

      {data
        .filter(category => category.products.length > 0)
        .map(category => (
          <section key={category.categoryId} className="mb-5">

            <div className="d-flex justify-content-between align-items-center mb-3">
              <h2 className="fw-bold">{category.categoryName}</h2>

              <Link
                to={`/products?categoryId=${category.categoryId}`}
                className="btn btn-outline-primary btn-sm"
              >
                View All →
              </Link>
            </div>

            <div className="row">
              {category.products.map(product => (
                <div key={product.id} className="col-6 col-md-3 mb-4">
                  <div className="card h-100 shadow-sm">
                    <img
                      src={API_BASE_URL + product.thumbnailUrl}
                      className="card-img-top"
                      alt={product.name}
                      style={{ height: "200px", objectFit: "cover" }}
                    />

                    <div className="card-body d-flex flex-column">
                      <h6 className="card-title">{product.name}</h6>

                      <p className="fw-bold text-danger mt-auto">
                        ${product.price}
                      </p>

                      <Link
                        to={`/products/${product.id}`}
                        className="btn btn-sm btn-dark mt-2"
                      >
                        View Details
                      </Link>
                    </div>
                  </div>
                </div>
              ))}
            </div>

          </section>
        ))}
    </div>
  );
}