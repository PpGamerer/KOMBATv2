import './globals.css';

export const metadata = {
  title: 'KOMBAT',
  description: 'A Next.js Game Application',
};


export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
