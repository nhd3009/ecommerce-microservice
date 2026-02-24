import { useEffect, useState } from "react";
import { filterProducts } from "../../../api/product.api";
import type { Product, ProductFilter } from "../../../types/product";
import type { Pagination } from "../../../types/pagination";

export const useProducts = (params?: ProductFilter) => {
  const [pageData, setPageData] = useState<Pagination<Product> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;

    const fetchProducts = async () => {
      try {
        setLoading(true);
        const res = await filterProducts(params);

        if (isMounted) {
          setPageData(res);
        }
      } catch (err) {
        if (isMounted) {
          setError("Products could not be loaded.");
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchProducts();

    return () => {
      isMounted = false;
    };
    
  }, [JSON.stringify(params)]); 

  return { pageData, loading, error };
};