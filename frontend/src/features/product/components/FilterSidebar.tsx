import type { ProductFilter } from "../../../types/product";
import { useCategories } from "../../category/hooks/useCategory";

interface Props {
  filters: ProductFilter;
  setFilters: React.Dispatch<React.SetStateAction<ProductFilter>>;
  onReset: () => void;
}

export function FilterSidebar({ filters, setFilters, onReset }: Props) {
  const { data: categories, loading } = useCategories();

  return (
    <div className="card p-3 shadow-sm">
      <h5 className="mb-3">Filter</h5>

      <input
        type="text"
        placeholder="Search name..."
        className="form-control mb-3"
        value={filters.name || ''}
        onChange={(e) =>
          setFilters(prev => ({ ...prev, name: e.target.value }))
        }
      />

      <input
        type="number"
        placeholder="Min price"
        className="form-control mb-2"
        value={filters.minPrice ?? ''}
        onChange={(e) =>
          setFilters(prev => ({
            ...prev,
            minPrice: e.target.value ? Number(e.target.value) : undefined,
          }))
        }
      />

      <input
        type="number"
        placeholder="Max price"
        className="form-control mb-3"
        value={filters.maxPrice ?? ''}
        onChange={(e) =>
          setFilters(prev => ({
            ...prev,
            maxPrice: e.target.value ? Number(e.target.value) : undefined,
          }))
        }
      />

      <div className="mb-3">
        <label className="form-label small fw-bold">Category</label>
        <select
          className="form-select"
          value={filters.categoryId || ""}
          disabled={loading}
          onChange={(e) =>
            setFilters((prev) => ({
              ...prev,
              categoryId: e.target.value ? Number(e.target.value) : undefined,
            }))
          }
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
        {loading && <div className="spinner-border spinner-border-sm mt-1"></div>}
      </div>

      <button
        className="btn btn-outline-secondary btn-sm w-100"
        onClick={onReset}
      >
        Reset
      </button>
    </div>
  );
}