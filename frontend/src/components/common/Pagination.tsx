type Props = {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
};

export default function Pagination({
  currentPage,
  totalPages,
  onPageChange,
}: Props) {

  return (
    <nav className="mt-4">
      <ul className="pagination justify-content-center">
        {Array.from({ length: totalPages }).map((_, i) => (
          <li key={i}
            className={`page-item ${i === currentPage ? "active" : ""}`}>
            <button className="page-link" onClick={() => onPageChange(i)}>
              {i + 1}
            </button>
          </li>
        ))}
      </ul>
    </nav>
  );
}