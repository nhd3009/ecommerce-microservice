import { useProducts } from "../hooks/useProducts";
import { API_BASE_URL } from "../../../utils/constants";
import Pagination from "../../../components/common/Pagination";
import { useState } from "react";
import Loading from "../../../components/common/Loading";
import Empty from "../../../components/common/Empty";
import { Link } from "react-router-dom";
import { FilterSidebar } from "./FilterSidebar";
import type { ProductFilter } from "../../../types/product";

export default function ProductList() {
  const [filters, setFilters] = useState<ProductFilter>({
    name: "",
    minPrice: undefined,
    maxPrice: undefined,
  });

  const [page, setPage] = useState(0);

  const { pageData, loading, error } = useProducts({ 
    page, 
    size: 12, 
    ...filters
  });

  const handleReset = () => {
    setFilters({ 
      name: "", 
      minPrice: undefined, 
      maxPrice: undefined,
      categoryId: undefined
    });
    setPage(0);
  };

  return (
    <div className="container-fluid mt-4">
      <div className="row">
        <div className="col-md-3">
          <FilterSidebar 
            filters={filters} 
            setFilters={setFilters} 
            onReset={handleReset} 
          />
        </div>
        <div className="col-md-9">
          {loading && <Loading />}
          {error && <p>Error loading products.</p>}

          {pageData && pageData.data.length > 0 ? (
            <>
              <div className="row g-3">
                {pageData.data.map((product) => (
                  <div key={product.id} className="col-lg-4 col-md-6"> 
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
                        <Link to={`/products/${product.id}`} className="btn btn-sm btn-dark mt-2">
                          View Details
                        </Link>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <Pagination
                currentPage={pageData.currentPage}
                totalPages={pageData.totalPages}
                onPageChange={setPage}
              />
            </>
          ) : (
            !loading && <Empty message="No products found" />
          )}
        </div>
      </div>
    </div>
  );
}