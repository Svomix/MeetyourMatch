'use client';
import { authed } from '@/services/axiosInstance';
import DateInput from '@components/Inputs/DateInput';
import LabelInput from '@components/Inputs/LabelInput';
import NumberInput from '@components/Inputs/NumberInput';
import TextInput from '@components/Inputs/TextInput';
import mock_event_img from '@public/mock_event_img.gif';
import routes from '@routes';
import { ModalPage, setModal, setModalData } from '@store/modalSlice';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styles from './page.module.css';

export default () => {
  const router = useRouter();
  const [title, setTitle] = useState('');
  const [date, setDate] = useState('');
  const place = useSelector((state) => state.modal.data.currentLocation);
  //const [place, setPlace] = useState('');
  const [price, setPrice] = useState('');
  const [desc, setDesc] = useState('');
  const [tags, setTags] = useState('');
  const [link, setLink] = useState('');
  const [img, setImg] = useState('');
  const [imgid, setImgid] = useState(null);

  const dispatch = useDispatch();

  const handleSubmit = async (e) => {
    e.preventDefault();

    let loc = location;

    if (place) {
      if (place.id === '?') {
        const new_place = {
          title: place.title,
          address: place.address,
          latitude: place.latitude,
          longitude: place.longitude
        };

        loc = (await authed.post('maps/locations', new_place)).data;
      }
    }

    const formData = new FormData(e.target);
    const body = Object.fromEntries(
      Array.from(formData.entries()).filter(([_, value]) => value !== '')
    );
    body['coverFileId'] = imgid;
    body['locationId'] = loc?.id;

    const resp = await authed.post('v1/events/uploadEvent', body);
    dispatch(setModalData({ key: 'currentLocation', data: undefined }));
    router.push(`/events/${resp.data.id}`);
  };

  const handleFileChange = (e) => {
    setImg(URL.createObjectURL(e.target.files[0]));
    const form = new FormData();
    form.append('file', e.target.files[0]);
    authed.post('v1/events/uploadEventImage', form).then((resp) => setImgid(resp.data.fileId));
  };

  const onChangeTitle = (e) => {
    const textarea = e.target;
    if (textarea.scrollHeight > parseFloat(getComputedStyle(textarea).lineHeight) * 3) {
      textarea.value = title;
    } else {
      let count = 0;
      let value = textarea.value.replace(/\n/g, () => (count++ < 2 ? '\n' : ''));
      setTitle(value);
    }
  };
  const onChangeDate = (e) => {
    setDate(e.target.value);
  };
  const onChangePlace = (e) => {
    e.preventDefault();
    dispatch(setModal(ModalPage.PlaceEditor));
  };
  const onChangePrice = (e) => {
    setPrice(e.target.value);
  };
  const onChangeText = (e) => {
    setDesc(e.target.value);
  };
  const onChangeTags = (e) => {
    setTags(e.target.value);
  };
  const onChangeLink = (e) => {
    setLink(e.target.value);
  };

  return (
    <form className={styles.wrapper} onSubmit={handleSubmit}>
      <div className={styles.img_section}>
        <div className={styles.left_side}>
          <TextInput
            name="title"
            required
            className={styles.title_input}
            text={title}
            onChange={onChangeTitle}
            placeholder={'Введите название'}
            maxLength={100}
          />

          <div className={styles.input_wrapper}>
            <label className={styles.meta}>Дата: </label>
            <DateInput name="date" date={date} onChange={onChangeDate} />
          </div>

          <div className={styles.input_wrapper}>
            <label className={styles.meta}>Место: </label>
            <button onClick={onChangePlace} className={styles.place_select}>
              {place ? place.title : 'Не указано'}
            </button>
          </div>

          <div className={styles.input_wrapper}>
            <label className={styles.meta}>Цена: </label>
            <NumberInput
              name="price"
              number={price}
              onChange={onChangePrice}
              placeholder="Не указано"
            />
          </div>

          <div className={styles.actions}>
            <div className={styles.input_wrapper}>
              <label className={styles.meta}>Ссылка на источник: </label>
              <LabelInput
                name="sourceUrl"
                label={link}
                onChange={onChangeLink}
                placeholder={'Не указано'}
              />
            </div>
          </div>
        </div>

        <div className={styles.right_side}>
          <input
            onChange={handleFileChange}
            className={styles.img_hover}
            type="file"
            accept="image/png, image/jpeg, image/gif"
          />
          <Image
            alt={'event image'}
            className={styles.img}
            src={img || mock_event_img}
            width={2000}
            height={2000}
          />
          <div className={styles.fade_out}></div>
        </div>
      </div>

      <div className={styles.extra_info}>
        <h4 className={styles.desc_name}>Описание:</h4>
        <TextInput
          required
          name="description"
          className={styles.desc_input}
          text={desc}
          onChange={onChangeText}
          maxLength={10000}
          placeholder={'Введите описание'}
        />
        <LabelInput
          name="tags"
          className={styles.tags_input}
          label={tags}
          onChange={onChangeTags}
          placeholder={'Введите теги ...'}
        />
      </div>

      <div className={styles.submit_wrapper}>
        <Link className={styles.submit_cancel} href={routes.SEARCH}>
          Отменить
        </Link>
        <button type="submit" className={styles.submit}>
          Создать
        </button>
      </div>
    </form>
  );
};
