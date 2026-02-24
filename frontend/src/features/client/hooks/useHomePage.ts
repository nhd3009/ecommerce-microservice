import { useEffect, useState } from "react";
import type { CategoryProduct } from "../../../types/categoryproduct";
import { getHomepageProducts } from "../../../api/product.api";

export const useHomepageProducts = () => {
  const [data, setData] = useState<CategoryProduct[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchHomepage = async () => {
      try {
        setLoading(true);
        const res = await getHomepageProducts();
        setData(res);
      } catch (err) {
        setError("Homepage data could not be loaded.");
      } finally {
        setLoading(false);
      }
    };

    fetchHomepage();
  }, []);

  return { data, loading, error };
};