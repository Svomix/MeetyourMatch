import CardInfo from '@components/CardInfo';
import mock_img from '@public/mock_img.jpg';

export default async function EventsPage({ params }) {
  const slug = (await params).id;

  return (
    <>
      <CardInfo path={slug} card={card} />
    </>
  );
}

const card = {
  title: 'Lorem ipsum dolor  ',
  description:
    'Lorem, ipsum dolor sit amet consectetur adipisicing elit. Commodi, ut. Minus possimus ea similique aliquam excepturi? Error magni sed ex, provident eos sit nisi cum distinctio officiis sequi. Reprehenderit laudantium magni doloribus rerum natus, doloremque perspiciatis fugit assumenda facilis officia dolores architecto ipsam quibusdam esse excepturi quis! Velit, a qui! Nam rerum, libero ipsum perspiciatis, laudantium molestiae labore quis quod, eum fuga architecto dolorum nobis esse! Doloremque blanditiis magni error, eligendi debitis nihil? Incidunt nemo dolores delectus magnam odio atque reiciendis quia. Sed enim, error, neque, labore necessitatibus cum rerum ducimus tempore quas dicta aspernatur hic deleniti dolores unde nesciunt!',
  date: '01.01.1970',
  place: 'Lorem ipsum dolor',
  price: 1001,
  tags: '#Lorem #ipsum #dolor',
  from: 'https://chatgpt.com/',
  img: mock_img,
  heart_count: 52
};
