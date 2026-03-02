interface EmptyProps {
  message?: string;
}

export default function Empty({
  message = "No data found",
}: EmptyProps) {
  return (
    <div className="text-center text-muted py-5">
      <p className="mb-0">{message}</p>
    </div>
  );
}