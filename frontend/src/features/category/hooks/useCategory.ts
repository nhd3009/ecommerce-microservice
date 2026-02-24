import { useEffect, useState } from "react";
import { getCategories, type Category } from "../../../api/category.api";


export const useCategories = () => {
  const [data, setData] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    getCategories()
      .then(res => {
        setData(res.data.data);
      })
      .catch(() => setError("Could not load categories"))
      .finally(() => setLoading(false));
  }, []);

  return { data, loading, error };
};
