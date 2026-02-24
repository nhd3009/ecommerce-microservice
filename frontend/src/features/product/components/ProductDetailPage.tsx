import { useParams } from "react-router-dom";
import { useProductDetail } from "../hooks/useProductDetail";
import { API_BASE_URL } from "../../../utils/constants";
import { useEffect, useState } from "react";

export default function ProductDetailPage() {
  const { id } = useParams();
  const productId = Number(id);

  const { product, loading, error } = useProductDetail(productId);
  const [mainImage, setMainImage] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (product?.thumbnailUrl) {
      setMainImage(product.thumbnailUrl);
    }
  }, [product]);

  if (loading) {
    return (
      <div className="text-center mt-5">
        <div className="spinner-border text-primary" />
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="alert alert-danger mt-5 text-center">
        {error || "Product not found"}
      </div>
    );
  }

  const isInactive = product.status === "INACTIVE";
  const isOutOfStock = product.status === "OUT_OF_STOCK";

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-md-6">
          <div className="main-image-container mb-3">
            <img
              src={API_BASE_URL + mainImage}
              alt={product.name}
              className="img-fluid rounded shadow w-100"
              style={{ height: "400px", objectFit: "cover" }}
            />
          </div>

          <div className="d-flex gap-2 overflow-auto pb-2">
            {[product.thumbnailUrl, ...product.imageUrls].map((imgUrl, index) => (
              <img
                key={index}
                src={API_BASE_URL + imgUrl}
                alt={`${product.name} ${index}`}
                className={`rounded border ${mainImage === imgUrl ? 'border-primary border-2' : ''}`}
                style={{ width: "80px", height: "80px", objectFit: "cover", cursor: "pointer" }}
                onClick={() => setMainImage(imgUrl)}
              />
            ))}
          </div>
        </div>

        <div className="col-md-6">
          <div className="d-flex justify-content-between align-items-start">
            <h2>{product.name}</h2>
            <span className={`badge ${product.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>
              {product.status}
            </span>
          </div>

          <h4 className="text-muted">Category: {product.categoryName}</h4>
          <h4 className="text-danger mb-3">${product.price}</h4>

          <div className="mb-3">
             <span className="fw-bold">Stock quantity:</span> 
             <span className={`ms-2 ${isOutOfStock ? 'text-danger' : ''}`}>
                {product.stockQuantity} units
             </span>
          </div>

          <p className="border-top pt-3">{product.description}</p>

          <hr />

          {isInactive ? (
            <div className="alert alert-secondary">No longer available</div>
          ) : isOutOfStock ? (
            <div className="alert alert-warning">Out of stock</div>
          ) : (
            <button className="btn btn-dark btn-lg w-100">
              Add to Cart
            </button>
          )}
        </div>
      </div>
    </div>
  );
}